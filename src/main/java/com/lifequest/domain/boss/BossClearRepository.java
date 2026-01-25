package com.lifequest.domain.boss;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface BossClearRepository extends JpaRepository<BossClear, Long> {
    Optional<BossClear> findByUserIdAndBossId(Long userId, Long bossId);
    List<BossClear> findByUserId(Long userId);
    boolean existsByUserIdAndBossId(Long userId, Long bossId);
    
    @Query("SELECT bc.boss.id FROM BossClear bc WHERE bc.user.id = :userId")
    List<Long> findClearedBossIdsByUserId(@Param("userId") Long userId);
}
