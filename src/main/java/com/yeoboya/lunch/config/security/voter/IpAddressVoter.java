package com.yeoboya.lunch.config.security.voter;

import com.yeoboya.lunch.config.security.domain.AccessIp;
import com.yeoboya.lunch.config.security.reqeust.ClientRequestInfo;
import com.yeoboya.lunch.config.security.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
public class IpAddressVoter implements AccessDecisionVoter<Object> {

    private final SecurityResourceService securityResourceService;

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
        //todo
        if (!(authentication.getDetails() instanceof ClientRequestInfo)) {
            log.debug("Authentication details is NOT an instance of ClientRequestInfo: {}", authentication.getDetails());
            return ACCESS_DENIED;
        }

        String address = ((ClientRequestInfo) authentication.getDetails()).getRemoteIp();
        log.debug("ClientRequestInfo address ->: {}", address);

        // IP가 차단 목록에 있고, isBlock이 true인 경우만 차단
        boolean isIpBlocked = securityResourceService.getAccessIpList().stream()
                .anyMatch(ip -> address.equals(ip.getIpAddress()) && ip.isBlock());


        if (isIpBlocked) {
            log.error("🚨 Blocked IP detected: {}", address);
            throw new AccessDeniedException("Invalid IP address: " + address + " cannot access this resource.");
        }

        return ACCESS_ABSTAIN; // 차단되지 않은 경우 투표 포기
    }
}
