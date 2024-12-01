package com.duantn.be_project.controller;

import com.duantn.be_project.security.model.ApiResponse;
import com.duantn.be_project.security.model.AuthenticateRequest;
import com.duantn.be_project.security.model.AuthenticationResponse;
import com.duantn.be_project.security.model.InstropecReponsee;
import com.duantn.be_project.security.service.authenticateService;
import com.duantn.be_project.security.service.jwtService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
@SecurityRequirement(name = "bearer-key")
@FieldDefaults(level =  AccessLevel.PRIVATE )
public class AuthenticateController {

    private final authenticateService authenticateService;
    private final jwtService jwtService;


    @PostMapping("/login")
    @CrossOrigin
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticateRequest request) {

        AuthenticationResponse result = authenticateService.authenticate(request);
        return  ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/refesh")
    ApiResponse<AuthenticationResponse>  authenticate(@RequestParam String token) throws ParseException, JOSEException {
        AuthenticationResponse result = jwtService.refeshToken(token);
        return  ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestParam String token) throws ParseException, JOSEException {
        authenticateService.Logout(token);
        return  ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("/instrospec")
    ApiResponse<InstropecReponsee> instropec(@RequestParam String token) throws ParseException, JOSEException {
        InstropecReponsee result = jwtService.instropecReponsee(token);
        return  ApiResponse.<InstropecReponsee>builder()
                .status(201)
                .result(result)
                .build();
    }

}
