package com.yeoboya.lunch.config.security.voter;

import com.yeoboya.lunch.config.security.domain.AccessIp;
import com.yeoboya.lunch.config.security.repository.AccessIpRepository;
import com.yeoboya.lunch.config.security.reqeust.ClientRequestInfo;
import com.yeoboya.lunch.config.security.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
public class IpAddressVoter implements AccessDecisionVoter<Object> {

    private final SecurityResourceService securityResourceService;
    private final AccessIpRepository accessIpRepository;

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return (attribute.getAttribute() != null);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }


    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> configList) {
        if (!(authentication.getDetails() instanceof ClientRequestInfo)) {
            log.debug("Authentication details is NOT an instance of ClientRequestInfo: {}", authentication.getDetails());
            return ACCESS_DENIED;
        }
        String address = ((ClientRequestInfo) authentication.getDetails()).getRemoteIp();

        AccessIp ipEntry = securityResourceService.getAccessIpList().stream()
                .filter(ip -> address.equals(ip.getIpAddress()))
                .findFirst()
                .orElse(null);

        if (ipEntry != null && ipEntry.isBlock()) {
            log.warn("Blocked IP: {}", address);

            // expiresAt이 설정되어 있고, 현재 시간 이후라면 차단 해제
            if (ipEntry.getExpiresAt() != null && ipEntry.getExpiresAt().isBefore(OffsetDateTime.now(ZoneId.of("Asia/Seoul")))) {
                ipEntry.setBlock(Boolean.FALSE);
                accessIpRepository.save(ipEntry);
                return ACCESS_ABSTAIN;
            }

            // 차단 상태에서 접근 시도 → hitCount 증가
            ipEntry.setHitCount(ipEntry.getHitCount() + 1);
            accessIpRepository.save(ipEntry);

            throw new AccessDeniedException("차단된 IP입니다. reason=" + ipEntry.getReason());
        }

        return ACCESS_ABSTAIN;
    }
}
