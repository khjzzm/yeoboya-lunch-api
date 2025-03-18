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
import java.util.List;
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
        String loginId = jwtTokenProvider.getJwtTokenSubject(request);
        Member member = myRepository.findMyInfo(loginId);
        return toMyInformationResponse(member);
    }

    // 내 정보 수정
    @Transactional
    public void editMyInfo(MemberInfoEdit memberInfoEdit, HttpServletRequest request) {
        String loginId = jwtTokenProvider.getJwtTokenSubject(request);
        Member member = myRepository.findMyInfo(loginId);

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
        String loginId = jwtTokenProvider.getJwtTokenSubject(request);
        Member member = myRepository.findMyInfo(loginId);
        return accountService.addAccount(member, accountCreate);
    }

    // 계좌 수정
    public void editAccountInfo(AccountEdit accountEdit, HttpServletRequest request) {
        String loginId = jwtTokenProvider.getJwtTokenSubject(request);
        Member member = myRepository.findMyInfo(loginId);
        accountService.editAccount(member, accountEdit);
    }

    // 프로필 등록
    public ResponseEntity<Response.Body> updateProfileImage(MultipartFile file, HttpServletRequest request) {
        String loginId = jwtTokenProvider.getJwtTokenSubject(request);
        Member member = myRepository.findMyInfo(loginId);

        // 파일 업로드
        Function<FileResponse, ProfileResponse> responseMapper = ProfileResponse::apply;
        ProfileResponse upload = fileServiceS3.upload(file, Directory.PROFILE, responseMapper);

        // 대표이미지 설정
        boolean isDefault = memberRepository.profileImg(loginId).stream()
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


    @Transactional
    public ResponseEntity<Response.Body> setDefaultProfileImage(Long imageNo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedId = Optional.of(authentication.getName()).orElseThrow(() -> new EntityNotFoundException(""));

        List<MemberProfileFile> byMemberLoginIdAndIsDefaultTrue = memberProfileFileRepository.findByMember_LoginIdAndIsDefaultTrue(loggedId);
        byMemberLoginIdAndIsDefaultTrue.forEach(profileFile -> profileFile.setIsDefault(false));

        MemberProfileFile defaultProfileImage = memberProfileFileRepository.findByMemberLoginIdAndId(loggedId, imageNo);
        defaultProfileImage.setIsDefault(true);

        return response.success(Code.UPDATE_SUCCESS);
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
