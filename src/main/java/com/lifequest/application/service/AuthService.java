package com.lifequest.application.service;

import com.lifequest.api.advice.ApiException;
import com.lifequest.api.advice.ErrorCode;
import com.lifequest.api.dto.request.LoginRequest;
import com.lifequest.api.dto.request.SignupRequest;
import com.lifequest.api.dto.response.AuthResponse;
import com.lifequest.domain.user.User;
import com.lifequest.domain.user.UserRepository;
import com.lifequest.domain.user.UserStats;
import com.lifequest.infrastructure.security.JwtProperties;
import com.lifequest.infrastructure.security.JwtService;
import com.lifequest.infrastructure.security.RefreshTokenStore;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenStore refreshTokenStore;
    private final JwtProperties properties;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenStore refreshTokenStore,
                       JwtProperties properties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenStore = refreshTokenStore;
        this.properties = properties;
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorCode.DUPLICATE_EMAIL, "Email already exists");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "Nickname already exists");
        }
        User user = User.create(
            request.getEmail(),
            request.getNickname(),
            passwordEncoder.encode(request.getPassword()),
            UUID.randomUUID()
        );
        user.initializeStats(UserStats.create());
        User saved = userRepository.save(user);
        return issueTokens(saved);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "User not found"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "Invalid credentials");
        }
        return issueTokens(user);
    }

    public AuthResponse refresh(String refreshToken) {
        Long userId = refreshTokenStore.findValidUserId(refreshToken)
            .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHORIZED, "Invalid refresh token"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "User not found"));
        refreshTokenStore.revoke(refreshToken);
        return issueTokens(user);
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(properties.refreshTokenExpiryDays(), ChronoUnit.DAYS);
        refreshTokenStore.save(refreshToken, user.getId(), expiresAt);
        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }
}
