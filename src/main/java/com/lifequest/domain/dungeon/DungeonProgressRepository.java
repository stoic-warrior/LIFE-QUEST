package com.lifequest.domain.dungeon;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DungeonProgressRepository extends JpaRepository<DungeonProgress, Long> {
    Optional<DungeonProgress> findByUserId(Long userId);
}
