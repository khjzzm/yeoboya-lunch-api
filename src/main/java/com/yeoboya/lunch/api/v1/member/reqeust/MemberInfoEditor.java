package com.yeoboya.lunch.api.v1.member.reqeust;


import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 정보를 업데이트하기 위한 DTO 클래스입니다.
 *
 * 이 클래스는 build 패턴을 따르고 있습니다. 따라서 클래스의 인스턴스 생성시에는 builder() 메소드를 호출 한 후
 * 각 필드의 값을 설정하고 마지막에 build() 메소드를 호출하여 인스턴스를 완성해야 합니다.
 *
 * 각 필드의 설정 메소드(bio, nickName, phoneNumber)는 null 값이 아닌 경우에만 값을 설정합니다.
 * 이를 통해 필드값을 선택적으로 업데이트 할 수 있습니다.
 */
@Getter
public class MemberInfoEditor {

    private String bio;
    private String nickName;
    private String phoneNumber;

    public MemberInfoEditor(String bio, String nickName, String phoneNumber) {
        this.bio = bio;
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
    }

    public static MemberInfoEditorBuilder builder() {
        return new MemberInfoEditorBuilder();
    }

    public static class MemberInfoEditorBuilder {
        private String bio;
        private String nickName;
        private String phoneNumber;


        public MemberInfoEditorBuilder bio(final String bio) {
            if (bio != null) {
                this.bio = bio;
            }
            return this;
        }

        public MemberInfoEditorBuilder nickName(final String nickName) {
            if (nickName != null) {
                this.nickName = nickName;
            }
            return this;
        }

        public MemberInfoEditorBuilder phoneNumber(final String phoneNumber) {
            if (phoneNumber != null) {
                this.phoneNumber = phoneNumber;
            }
            return this;
        }

        public MemberInfoEditor build() {
            return new MemberInfoEditor(this.bio, this.nickName, this.phoneNumber);
        }

        public String toString() {
            return "MemberInfoEdit.MemberInfoEditBuilder(bio=" + this.bio + ", nickName=" + this.nickName + ", phoneNumber=" + this.phoneNumber + ")";
        }
    }


}
