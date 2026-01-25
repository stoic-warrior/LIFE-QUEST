package com.lifequest.domain.hunting;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HuntingGroundRepository extends JpaRepository<HuntingGround, Long> {
    List<HuntingGround> findAllByOrderByRequiredLevelAsc();
}
