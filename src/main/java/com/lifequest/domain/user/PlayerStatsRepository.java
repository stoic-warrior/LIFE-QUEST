package com.lifequest.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PlayerStatsRepository extends JpaRepository<PlayerStats, Long> {
    Optional<PlayerStats> findByUserId(Long userId);
}
