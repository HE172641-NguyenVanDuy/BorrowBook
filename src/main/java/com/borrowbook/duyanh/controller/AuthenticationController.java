package com.borrowbook.duyanh.controller;

import com.borrowbook.duyanh.dto.request.AuthenticationRequest;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.dto.response.AuthenticationResponse;
import com.borrowbook.duyanh.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping(name = "/auth")
//public class AuthenticationController {
//    @Autowired
//    private AuthenticationService authenticationService;
//
//    @PostMapping("/token")
//    public ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
//        var result = authenticationService.authenticate(authenticationRequest);
//        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
//    }
//
//
//
//}
