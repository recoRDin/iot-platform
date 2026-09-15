package com.iot.server.auth.controller;


import com.iot.core.tool.api.R;
import com.iot.server.auth.dto.LoginRequest;
import com.iot.server.auth.model.AuthenticatedUser;
import com.iot.server.auth.service.IAuthService;
import com.iot.server.auth.token.JwtTokenService;
import com.iot.server.auth.token.TokenResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IAuthService authService;

    private final JwtTokenService jwtTokenService;

    public AuthController(IAuthService authService, JwtTokenService jwtTokenService) {
        this.authService = authService;
        this.jwtTokenService = jwtTokenService;
    }


    @PostMapping("/login")
    public R<TokenResult> login(@Valid @RequestBody LoginRequest request) {

        AuthenticatedUser user = authService.authenticate(request);
        TokenResult token = jwtTokenService.createAccessToken(user);

        return R.data(token);
    }
}
