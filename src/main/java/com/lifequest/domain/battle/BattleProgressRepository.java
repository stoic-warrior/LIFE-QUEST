package com.lifequest.domain.battle;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BattleProgressRepository extends JpaRepository<BattleProgress, Long> {
    Optional<BattleProgress> findByUserId(Long userId);
}
