package com.example.springlibrary.security.service;

import com.example.springlibrary.security.entity.RefreshToken;
import com.example.springlibrary.security.entity.User;
import com.example.springlibrary.security.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    private final long refreshTokenDurationMs = 7L * 24 * 60 * 60 * 1000;

    @Transactional
    public RefreshToken createRefreshToken(User user){
        deleteByUser(user);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public boolean isExpired(RefreshToken refreshToken){
        return refreshToken.getExpiryDate().isBefore(Instant.now());
    }

    @Transactional
    public void deleteByUser(User user){
        refreshTokenRepository.deleteByUser(user);
    }
}
