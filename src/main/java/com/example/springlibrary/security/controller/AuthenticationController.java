package com.example.springlibrary.security.controller;

import com.example.springlibrary.security.dto.code.AuthCodeVerficationDto;
import com.example.springlibrary.security.dto.auth.AuthenticationRequestDto;
import com.example.springlibrary.security.dto.common.AuthenticationResponseDto;
import com.example.springlibrary.security.dto.auth.RegisterRequestDto;
import com.example.springlibrary.security.dto.refreshtoken.TokenRefreshRequestDto;
import com.example.springlibrary.security.dto.refreshtoken.TokenRefreshResponseDto;
import com.example.springlibrary.security.entity.User;
import com.example.springlibrary.security.repository.RefreshTokenRepository;
import com.example.springlibrary.security.service.AuthenticationService;
import com.example.springlibrary.security.service.JwtService;
import com.example.springlibrary.security.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDto request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/send-code")
    public ResponseEntity<String> authenticate(@Valid @RequestBody AuthenticationRequestDto request) {
        return ResponseEntity.ok(service.authenticateSendCode(request));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<AuthenticationResponseDto> verifyCode(@Valid @RequestBody AuthCodeVerficationDto authCodeVerfication) {
        return ResponseEntity.ok(service.verifyCode(authCodeVerfication));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequestDto tokenRefresh){
        return refreshTokenRepository.findRefreshTokenByToken(tokenRefresh.getRefreshToken())
                .map(token->{
                    if (refreshTokenService.isExpired(token)){
                        refreshTokenRepository.delete(token);
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired. Please log in again.");
                    }
                    User user = token.getUser();
                    String newAccessToken = jwtService.generatedToken(user);
                    return ResponseEntity.ok(new TokenRefreshResponseDto(newAccessToken,token.getToken()));

                })
                .orElseGet(()->ResponseEntity.status(HttpStatus.NOT_FOUND).body("Refresh Token Not Found"));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody TokenRefreshRequestDto tokenRefreshRequestDto){
        return refreshTokenRepository.findRefreshTokenByToken(tokenRefreshRequestDto.getRefreshToken())
                .map(refreshToken -> {
                    refreshTokenService.deleteByUser(refreshToken.getUser());
                    return ResponseEntity.ok("User logged out successfully");
                }).orElseGet(()->ResponseEntity.status(HttpStatus.NOT_FOUND).body("Refresh Token Not Found"));
    }
}
