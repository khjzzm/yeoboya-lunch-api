package com.yeoboya.lunch.api.v1.profile.service;

import com.yeoboya.lunch.api.v1.common.exception.EntityNotFoundException;
import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.ErrorCode;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.file.constant.Directory;
import com.yeoboya.lunch.api.v1.file.domain.MemberProfileFile;
import com.yeoboya.lunch.api.v1.file.repository.MemberProfileFileRepository;
import com.yeoboya.lunch.api.v1.file.response.FileResponse;
import com.yeoboya.lunch.api.v1.file.response.ProfileResponse;
import com.yeoboya.lunch.api.v1.file.service.FileServiceS3;
import com.yeoboya.lunch.api.v1.member.domain.Member;
import com.yeoboya.lunch.api.v1.member.domain.MemberInfo;
import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import com.yeoboya.lunch.api.v1.member.reqeust.*;
import com.yeoboya.lunch.api.v1.member.response.AccountResponse;
import com.yeoboya.lunch.api.v1.member.service.AccountService;
import com.yeoboya.lunch.api.v1.profile.repository.MyRepository;
import com.yeoboya.lunch.api.v1.profile.response.MyInformation;
import com.yeoboya.lunch.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.function.Function;

import static com.yeoboya.lunch.api.v1.profile.response.MyInformation.toMyInformationResponse;

@Service
@RequiredArgsConstructor
public class MyService {

    private final Response response;

    private final AccountService accountService;
    private final FileServiceS3 fileServiceS3;

    private final MyRepository myRepository;
    private final MemberRepository memberRepository;
    private final MemberProfileFileRepository memberProfileFileRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 내 정보
    public MyInformation getMyInformation(HttpServletRequest request) {
        String loggedId = this.getLoginId(request);
        Member member = myRepository.findMyInfo(loggedId);
        return toMyInformationResponse(member);
    }

    // 내 정보 수정
    @Transactional
    public void editMyInfo(MemberInfoEdit memberInfoEdit, HttpServletRequest request) {
        String loggedId = this.getLoginId(request);
        Member member = myRepository.findMyInfo(loggedId);

        MemberInfo memberInfo = member.getMemberInfo();

        MemberInfoEditor memberInfoEditor = memberInfo.toEditor()
                .bio(memberInfoEdit.getBio())
                .phoneNumber(memberInfoEdit.getPhoneNumber())
                .build();

        try {
            memberInfo.edit(memberInfoEditor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 계좌 등록
    public AccountResponse saveMyAccount(AccountCreate accountCreate, HttpServletRequest request) {
        String loggedId = this.getLoginId(request);
        Member member = myRepository.findMyInfo(loggedId);
        return accountService.addAccount(member, accountCreate);
    }

    // 계좌 수정
    public void editAccountInfo(AccountEdit accountEdit, HttpServletRequest request) {
        String loggedId = this.getLoginId(request);
        Member member = myRepository.findMyInfo(loggedId);
        accountService.editAccount(member, accountEdit);
    }

    // 프로필 등록
    public ResponseEntity<Response.Body> updateProfileImage(MultipartFile file, HttpServletRequest request) {
        String loggedId = this.getLoginId(request);
        Member member = myRepository.findMyInfo(loggedId);

        // 현재 회원의 프로필 사진 개수 조회
        long profileImageCount = memberRepository.profileImg(loggedId).size();

        // 최대 10개 제한
        if (profileImageCount >= 10) {
            return response.fail(ErrorCode.TOO_MANY_REQUESTS, "프로필 사진은 최대 10개까지만 등록할 수 있습니다.");
        }

        // 파일 업로드
        Function<FileResponse, ProfileResponse> responseMapper = ProfileResponse::apply;
        ProfileResponse upload = fileServiceS3.upload(file, Directory.PROFILE, responseMapper);

        // 대표이미지 설정
        boolean isDefault = memberRepository.profileImg(loggedId).stream()
                .anyMatch(MemberProfileFile::getIsDefault);
        upload.setIsDefault(!isDefault);

        // 엔티티 생성
        MemberProfileFile profileFileEntity = MemberProfileFile.builder()
                .member(member)
                .profileResponse(upload)
                .build();

        MemberProfileFile memberProfileFile = memberProfileFileRepository.save(profileFileEntity);
        ProfileResponse profileResponse = ProfileResponse.from(memberProfileFile);
        return response.success(Code.UPDATE_SUCCESS, profileResponse);
    }

    public ResponseEntity<Response.Body> deleteProfileImage(Long imageNo, HttpServletRequest request) {
        String loggedId = this.getLoginId(request);

        MemberProfileFile profileImage = memberProfileFileRepository.findById(imageNo)
                .orElseThrow(() -> new EntityNotFoundException("해당 프로필 이미지를 찾을 수 없습니다."));

        if (!profileImage.getMember().getLoginId().equals(loggedId)) {
            return response.fail(ErrorCode.FORBIDDEN);
        }

        memberProfileFileRepository.delete(profileImage);

        boolean wasDefault = profileImage.getIsDefault(); // 기존 대표 사진 여부
        if (wasDefault) {
            Optional<MemberProfileFile> latestProfileImage = memberProfileFileRepository
                    .findTopByMemberOrderByIdDesc(profileImage.getMember());

            latestProfileImage.ifPresent(img -> {
                img.setIsDefault(true);
                memberProfileFileRepository.save(img);
            });
        }

        return response.success(Code.DELETE_SUCCESS);

    }

    @Transactional
    public ResponseEntity<Response.Body> setDefaultProfileImage(Long imageNo, HttpServletRequest request) {
        String loggedId = this.getLoginId(request);

        // 기존 기본 프로필 해제
        memberProfileFileRepository.resetDefaultProfileImage(loggedId);

        // 새로운 기본 프로필 설정
        MemberProfileFile defaultProfileImage = memberProfileFileRepository.findByMemberLoginIdAndId(loggedId, imageNo)
                .orElseThrow(() -> new EntityNotFoundException("해당 프로필 이미지를 찾을 수 없습니다."));

        defaultProfileImage.setIsDefault(true);

        return response.success(Code.UPDATE_SUCCESS);
    }


    // Spring Security를 통한 ID 가져오기, SecurityContext가 없는 경우, JWT에서 직접 ID 추출
    public String getLoginId(HttpServletRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }

        return jwtTokenProvider.getJwtTokenSubject(request);
    }

    // self check
    public void isMe(String loginId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = Optional.of(authentication.getName()).orElseThrow(() -> new EntityNotFoundException(""));
        if (!loginId.equals(loggedInUsername)) {
            throw new EntityNotFoundException(loginId);
        }
    }


}
