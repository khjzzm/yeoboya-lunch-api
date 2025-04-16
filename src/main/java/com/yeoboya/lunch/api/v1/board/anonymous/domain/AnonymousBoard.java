package com.yeoboya.lunch.api.v1.board.anonymous.domain;

import com.yeoboya.lunch.api.v1.board.anonymous.reqeust.AnonymousBoardCreate;
import com.yeoboya.lunch.api.v1.board.base.domain.AbstractBoard;
import com.yeoboya.lunch.api.v1.board.base.domain.PinSupport;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@DiscriminatorValue("ANONYMOUS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AnonymousBoard extends AbstractBoard implements PinSupport {

    @Column(name = "NICKNAME")
    private String nickname;

    @Column(name = "WRITER_IP_HASH")
    private String writerIpHash;    // 실제 IP는 저장하지 않고, 해시로 저장하여 같은 사용자임을 식별 가능하게끔.

    @Column(name = "DELETE_AT")
    private LocalDateTime deleteAt; // 사용자가 설정한 삭제 예정 시간

    @Column(name = "PASSWORD_HASH")
    private String passwordHash;

    @Column(name = "REPORT_COUNT")
    private int reportCount;    //유저들이 해당 글을 신고했을 때 집계되는 값

    @Override
    public String getPin() {
        return this.passwordHash;
    }

    public static AnonymousBoard toEntity(AnonymousBoardCreate create, String ipHash, String passwordHash) {
        AnonymousBoard anonymousBoard = new AnonymousBoard();
        anonymousBoard.setContent(create.getContent());
        anonymousBoard.setNickname(create.getNickname());
        anonymousBoard.setWriterIpHash(ipHash);
        anonymousBoard.setDeleteAt(create.getDeleteAt());
        anonymousBoard.setPasswordHash(passwordHash);
        anonymousBoard.setReportCount(0);
        return anonymousBoard;
    }

}
