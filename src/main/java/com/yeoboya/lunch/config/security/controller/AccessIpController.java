package com.yeoboya.lunch.config.security.controller;

import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.config.security.reqeust.AccessIpRequest;
import com.yeoboya.lunch.config.security.response.AccessIpResponse;
import com.yeoboya.lunch.config.security.service.AccessIpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/access-ip")
@RequiredArgsConstructor
@Slf4j
public class AccessIpController {

    private final AccessIpService accessIpService;
    private final Response response;

    @GetMapping
    public ResponseEntity<Response.Body> getAll() {
        List<AccessIpResponse> accessIpServiceAll = accessIpService.findAll();
        return response.success(Code.SEARCH_SUCCESS, accessIpServiceAll);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<Response.Body> create(@RequestBody AccessIpRequest accessIpRequest) {
        log.error(accessIpRequest.toString());
        AccessIpResponse accessIpResponse = accessIpService.save(accessIpRequest);
        return response.success(Code.SAVE_SUCCESS, accessIpResponse);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping
    public ResponseEntity<Response.Body> update(@RequestParam Long id, @RequestBody AccessIpRequest accessIpRequest) {
        log.error(accessIpRequest.toString());
        AccessIpResponse accessIpResponse = accessIpService.update(id, accessIpRequest);
        return response.success(Code.UPDATE_SUCCESS, accessIpResponse);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping
    public ResponseEntity<Response.Body> delete(@RequestParam Long id) {
        accessIpService.delete(id);
        return response.success(Code.DELETE_SUCCESS);
    }
} 
