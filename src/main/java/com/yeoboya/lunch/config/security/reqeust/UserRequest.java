package com.yeoboya.lunch.config.security.reqeust;

import com.yeoboya.lunch.config.security.validation.CustomUniqueValue;
import com.yeoboya.lunch.config.security.validation.FieldType;
import com.yeoboya.lunch.config.security.validation.ValidationGroups;
import com.yeoboya.lunch.config.security.validation.ValidationGroups.KnowOldPassword;
import com.yeoboya.lunch.config.security.validation.ValidationGroups.UnKnowOldPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class UserRequest {

    @Getter
    @Setter
    @ToString
    @Schema(name = "SignUp", description = "회원가입 요청 객체")
    public static class SignUp {

        @Schema(description = "사용자의 로그인 아이디", example = "yeoboya123")
        @NotEmpty(message = "로그인 아이디는 필수 입력값입니다.")
        @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_.]{1,60}$", message = "아이디는 영어 또는 숫자로 시작해야 하며, 2~60자여야 합니다.")
        @CustomUniqueValue(fieldType = FieldType.LOGIN_ID, message = "이미 존재하는 아이디입니다.", groups = ValidationGroups.NormalSignUpGroup.class)
        private String loginId;

        @Schema(description = "사용자의 이메일 주소", example = "user@example.com")
        @NotEmpty(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @CustomUniqueValue(fieldType = FieldType.EMAIL, message = "이미 존재하는 이메일입니다.", groups = ValidationGroups.NormalSignUpGroup.class)
        private String email;

        @Schema(description = "사용자의 이름 (2~50자)", example = "홍길동")
        @Length(min = 2, max = 50)
        @NotEmpty(message = "이름은 필수 입력값입니다.")
        private String name;

        @Schema(description = "사용자의 비밀번호 (8~16자, 대소문자, 숫자, 특수문자 포함)", example = "P@ssw0rd!")
        @NotEmpty(message = "비밀번호는 필수입니다.", groups = ValidationGroups.NormalSignUpGroup.class)
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$",
                message = "비밀번호는 8~16자 영문/숫자/특수문자 포함", groups = ValidationGroups.NormalSignUpGroup.class)
        private String password;

        @Schema(description = "회원가입 제공자 정보 (기본값: yeoboya)", example = "yeoboya")
        private String provider = "yeoboya";

        private String nickName;
    }


    @Getter
    @Setter
    @ToString(callSuper = true)
    @Schema(name = "SocialSignUp", description = "소셜 회원가입 요청 객체")
    public static class SocialSignUp extends SignUp {
        @Schema(description = "소셜 프로필 이미지 URL", example = "https://lh3.googleusercontent.com/a/default-profile.jpg")
        private String profileImageUrl;

    }

    @Getter
    @Setter
    @ToString
    @Schema(name = "Credentials", description = "비밀번호 변경 요청 객체")
    public static class Credentials {

        @Schema(description = "사용자의 로그인 아이디", example = "yeoboya123")
        @NotEmpty(message = "로그인 아이디는 필수 입력값입니다.")
        @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_.]{1,60}$", message = "아이디는 영어 또는 숫자로 시작해야 하며, 2~60자여야 합니다.")
        private String loginId;

        @Schema(description = "사용자의 이메일 주소", example = "user@example.com")
        @NotEmpty(message = "이메일은 필수 입력값입니다.")
        @Email
        private String email;

        @Schema(description = "현재 비밀번호", example = "P@ssw0rd!")
        @NotEmpty(message = "비밀번호는 필수 입력값입니다.", groups = KnowOldPassword.class)
        private String oldPassword;

        @Schema(description = "새로운 비밀번호", example = "NewP@ssw0rd1!")
        @NotEmpty(message = "비밀번호는 필수 입력값입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$",
                message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        private String newPassword;

        @Schema(description = "새로운 비밀번호 확인", example = "NewP@ssw0rd1!")
        @NotEmpty(message = "비밀번호는 필수 입력값입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$",
                message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        private String confirmNewPassword;

        @Schema(description = "비밀번호 초기화 키", example = "abcdef123456")
        @NotEmpty(message = "please enter passKey", groups = UnKnowOldPassword.class)
        private String passKey;
    }

    @Getter
    @Setter
    @ToString
    @Schema(name = "SignIn", description = "로그인 요청 객체")
    public static class SignIn {

        @Schema(description = "사용자의 로그인 아이디", example = "yeoboya123")
        @NotEmpty(message = "로그인 아이디는 필수 입력값입니다.")
        private String loginId;

        @Schema(description = "사용자의 비밀번호", example = "P@ssw0rd!")
        @NotEmpty(message = "비밀번호는 필수 입력값입니다.")
        private String password;

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(loginId, password);
        }
    }

    @Getter
    @Setter
    @Schema(name = "SignOut", description = "로그아웃 요청 객체")
    public static class SignOut {

        @Schema(description = "사용자의 액세스 토큰", example = "eyJhbGciOiJIUzI1...")
        @NotEmpty(message = "accessToken 을 입력해주세요.")
        private String accessToken;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @Schema(name = "Reissue", description = "토큰 재발급 요청 객체")
    public static class Reissue {

        @Schema(description = "사용자의 리프레시 토큰", example = "eyJhbGciOiJIUzI1...")
        @NotEmpty(message = "refreshToken 을 입력해주세요.")
        private String refreshToken;

        public Reissue(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @Schema(name = "ResetPassword", description = "비밀번호 재설정 요청 객체")
    public static class ResetPassword {

        @NotEmpty(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Schema(description = "사용자의 이메일", example = "user@example.com")
        private String email;

        @NotEmpty(message = "아이디는 필수 입력값입니다.")
        @Schema(description = "사용자의 로그인 아이디", example = "yeoboya123")
        private String loginId;

        @Schema(description = "회원가입 제공자 정보 (기본값: yeoboya)", example = "yeoboya")
        private String provider = "yeoboya";

        @Schema(description = "비밀번호 재설정 링크", example = "/user/reset/password")
        private String authorityPage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @Schema(description = "회원 탈퇴 요청 객체")
    public static class WithdrawRequest {

        @NotEmpty(message = "로그인 아이디는 필수입니다.")
        @Schema(description = "회원의 로그인 ID", example = "yeoboya123")
        private String loginId;

        @NotEmpty(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Schema(description = "회원의 이메일", example = "user@example.com")
        private String email;

        @Schema(description = "회원가입 제공자 정보 (기본값: yeoboya)", example = "yeoboya")
        private String provider = "yeoboya";

        @Schema(description = "탈퇴 사유", example = "더 이상 서비스를 사용하지 않음")
        private String reason;

    }
}
