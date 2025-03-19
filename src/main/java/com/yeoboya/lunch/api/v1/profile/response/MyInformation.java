package com.yeoboya.lunch.api.v1.profile.response;

import com.yeoboya.lunch.api.v1.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyInformation {
    private String loginId;
    private String email;
    private String name;
    private String provider;
    private String roleDesc;
    private Account account;
    private Info info;
    private List<ProfileImage> profileImages;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Account {
        private String bankName;
        private String accountNumber;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private String nickName;
        private String bio;
        private String phoneNumber;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileImage {
        private Long profileImageNo;
        private String fileName;
        private String imageUrl;
        private String thumbnailUrl;
        private Boolean isDefault;
    }

    public static MyInformation toMyInformationResponse(Member member) {
        if (member == null) {
            return null;
        }

        // Account 변환 (기본값 적용)
        MyInformation.Account account = (member.getAccount() != null) ?
                MyInformation.Account.builder()
                        .bankName(member.getAccount().getBankName())
                        .accountNumber(member.getAccount().getAccountNumber())
                        .build()
                : null;

        // Info 변환 (기본값 적용)
        MyInformation.Info info = (member.getMemberInfo() != null) ?
                MyInformation.Info.builder()
                        .nickName(member.getMemberInfo().getNickName())
                        .bio(member.getMemberInfo().getBio())
                        .phoneNumber(member.getMemberInfo().getPhoneNumber())
                        .build()
                : MyInformation.Info.builder()
                .nickName("닉네임 없음")
                .bio("소개 없음")
                .phoneNumber("전화번호 없음")
                .build();

        // 프로필 사진 변환 (여러 개일 수 있음)
        List<MyInformation.ProfileImage> profileImages = (member.getMemberProfileFiles() != null) ?
                member.getMemberProfileFiles().stream()
                        .map(file -> ProfileImage.builder()
                                .profileImageNo(file.getId())
                                .fileName(file.getFileName())
                                .imageUrl(file.getImageUrl())
                                .thumbnailUrl(file.getThumbnailUrl())
                                .isDefault(file.getIsDefault())
                                .build())
                        .collect(Collectors.toList())
                : null;


        // 최종 응답 객체 변환
        return MyInformation.builder()
                .loginId(member.getLoginId())
                .email(member.getEmail())
                .name(member.getName())
                .provider(member.getProvider())
                .roleDesc(member.getRole() != null ? member.getRole().getRoleDesc() : "역할 없음")
                .account(account)
                .info(info)
                .profileImages(profileImages)
                .build();
    }
}