package com.lifequest.domain.boss;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BossSummonRepository extends JpaRepository<BossSummon, Long> {
    List<BossSummon> findAllByOrderByPriceAsc();
    Optional<BossSummon> findByBossId(Long bossId);
}
