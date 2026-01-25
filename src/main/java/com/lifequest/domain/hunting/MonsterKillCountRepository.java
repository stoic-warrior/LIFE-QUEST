package com.lifequest.domain.hunting;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MonsterKillCountRepository extends JpaRepository<MonsterKillCount, Long> {
    Optional<MonsterKillCount> findByUserIdAndMonsterId(Long userId, Long monsterId);
    List<MonsterKillCount> findByUserId(Long userId);
}
