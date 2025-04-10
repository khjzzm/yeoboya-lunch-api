package com.yeoboya.lunch.config.security.controller;

import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.config.security.reqeust.AccessIpRequest;
import com.yeoboya.lunch.config.security.response.AccessIpResponse;
import com.yeoboya.lunch.config.security.service.AccessIpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/access-ip")
@RequiredArgsConstructor
public class AccessIpController {

    private final AccessIpService accessIpService;
    private final Response response;

    @GetMapping
    public ResponseEntity<Response.Body> getAll() {
        List<AccessIpResponse> accessIpServiceAll = accessIpService.findAll();
        return response.success(Code.SEARCH_SUCCESS, accessIpServiceAll);
    }

    @PostMapping
    public ResponseEntity<Response.Body> create(@RequestBody AccessIpRequest accessIpRequest) {
        AccessIpResponse accessIpResponse = accessIpService.save(accessIpRequest);
        return response.success(Code.SAVE_SUCCESS, accessIpResponse);
    }

    @PutMapping
    public ResponseEntity<Response.Body> update(@RequestParam Long id, @RequestBody AccessIpRequest accessIpRequest) {
        AccessIpResponse accessIpResponse = accessIpService.update(id, accessIpRequest);
        return response.success(Code.UPDATE_SUCCESS, accessIpResponse);
    }

    @DeleteMapping
    public ResponseEntity<Response.Body> delete(@RequestParam Long id) {
        accessIpService.delete(id);
        return response.success(Code.DELETE_SUCCESS);
    }
} 
