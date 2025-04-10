package com.yeoboya.lunch.config.security.domain;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "ACCESS_IP")
@Data
@EntityListeners(value = { AuditingEntityListener.class})
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessIp implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "IP_ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "IP_ADDRESS", nullable = false)
    private String ipAddress;

    @Column(name = "BLOCK", nullable = false)
    private boolean block;

    //차단 사유
    @Column(name = "REASON")
    private String reason;

    //일시적 차단 해제 예정 시간
    @Column(name = "EXPIRES_AT")
    private LocalDateTime expiresAt;

    //차단 후 접근 시도 횟수
    @Column(name = "HIT_COUNT")
    private int hitCount;

}
