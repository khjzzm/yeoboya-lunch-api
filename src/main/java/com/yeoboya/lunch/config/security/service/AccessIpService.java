package com.yeoboya.lunch.config.security.service;

import com.yeoboya.lunch.config.security.domain.AccessIp;
import com.yeoboya.lunch.config.security.repository.AccessIpRepository;
import com.yeoboya.lunch.config.security.reqeust.AccessIpRequest;
import com.yeoboya.lunch.config.security.response.AccessIpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccessIpService {

    private final AccessIpRepository accessIpRepository;

    public List<AccessIpResponse> findAll() {
        List<AccessIp> all = accessIpRepository.findAll();
        return all.stream()
                .map(AccessIpResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccessIpResponse save(AccessIpRequest accessIpRequest) {
        AccessIp entity = AccessIp.builder()
                .ipAddress(accessIpRequest.getIpAddress())
                .block(accessIpRequest.isBlock())
                .reason(accessIpRequest.getReason())
                .expiresAt(accessIpRequest.getExpiresAt())
                .hitCount(accessIpRequest.getHitCount())
                .build();
        AccessIp save = accessIpRepository.save(entity);
        return AccessIpResponse.from(save);
    }

    @Transactional
    public AccessIpResponse update(Long id, AccessIpRequest accessIpRequest) {
        AccessIp ip = accessIpRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("IP not found: " + id));
        ip.setIpAddress(accessIpRequest.getIpAddress());
        ip.setBlock(accessIpRequest.isBlock());
        ip.setReason(accessIpRequest.getReason());
        ip.setExpiresAt(accessIpRequest.getExpiresAt());
        ip.setHitCount(accessIpRequest.getHitCount());
        AccessIp save = accessIpRepository.save(ip);
        return AccessIpResponse.from(save);
    }


    public void delete(Long id) {
        try {
            accessIpRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}