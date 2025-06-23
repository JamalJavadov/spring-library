package com.example.springlibrary.security.service;

import com.example.springlibrary.exception.CodeExpire;
import com.example.springlibrary.security.dto.code.AuthCodeVerficationDto;
import com.example.springlibrary.security.dto.auth.AuthenticationRequestDto;
import com.example.springlibrary.security.dto.common.AuthenticationResponseDto;
import com.example.springlibrary.security.dto.auth.RegisterRequestDto;
import com.example.springlibrary.security.notification.EmailService;
import com.example.springlibrary.security.notification.VerificationCodeStore;
import com.example.springlibrary.security.entity.Role;
import com.example.springlibrary.security.entity.User;
import com.example.springlibrary.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final VerificationCodeStore verificationCodeStore;
    private final RefreshTokenService refreshTokenService;

    public String register(RegisterRequestDto request) {
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        return "Register Successfully";
    }

    public String authenticateSendCode(AuthenticationRequestDto request) {
        String code = String.valueOf(new Random().nextInt(900000)+100000);
        verificationCodeStore.saveCode(request.getEmail(),code);
        emailService.sendVerificationCode(request.getEmail(),code);
        return "Verification Code Send";
    }

    public AuthenticationResponseDto verifyCode(AuthCodeVerficationDto authCodeVerfication) {
        if (verificationCodeStore.isCodeValid(authCodeVerfication.getEmail(), authCodeVerfication.getCode())) {
            verificationCodeStore.removeCode(authCodeVerfication.getEmail());
            var user = repository.findByEmail(authCodeVerfication.getEmail()).orElseThrow();
            var jwtToken = jwtService.generatedToken(user);
            String refreshToken = refreshTokenService.createRefreshToken(user).getToken();
            return AuthenticationResponseDto.builder().token(jwtToken).refreshToken(refreshToken).build();
        } else {
            throw new CodeExpire("code tap tap");
        }
    }
}
