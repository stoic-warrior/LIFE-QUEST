package com.lifequest.domain.battle;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BattleLogRepository extends JpaRepository<BattleLog, Long> {
    List<BattleLog> findTop20ByUserIdOrderByCreatedAtDesc(Long userId);
}
