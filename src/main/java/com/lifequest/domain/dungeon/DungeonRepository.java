package com.lifequest.domain.dungeon;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DungeonRepository extends JpaRepository<Dungeon, Long> {
    List<Dungeon> findAllByOrderByFloorNumberAsc();
    List<Dungeon> findByRequiredLevelLessThanEqualOrderByFloorNumberAsc(int level);
}
