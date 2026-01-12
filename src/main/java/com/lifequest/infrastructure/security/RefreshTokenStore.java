package com.lifequest.infrastructure.security;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenStore {
    void save(String token, Long userId, Instant expiresAt);

    Optional<Long> findValidUserId(String token);

    void revoke(String token);
}
