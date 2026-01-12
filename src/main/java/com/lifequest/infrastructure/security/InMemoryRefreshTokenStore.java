package com.lifequest.infrastructure.security;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryRefreshTokenStore implements RefreshTokenStore {
    private final Map<String, TokenEntry> storage = new ConcurrentHashMap<>();

    @Override
    public void save(String token, Long userId, Instant expiresAt) {
        storage.put(token, new TokenEntry(userId, expiresAt));
    }

    @Override
    public Optional<Long> findValidUserId(String token) {
        TokenEntry entry = storage.get(token);
        if (entry == null) {
            return Optional.empty();
        }
        if (Instant.now().isAfter(entry.expiresAt())) {
            storage.remove(token);
            return Optional.empty();
        }
        return Optional.of(entry.userId());
    }

    @Override
    public void revoke(String token) {
        storage.remove(token);
    }

    private record TokenEntry(Long userId, Instant expiresAt) {
    }
}
