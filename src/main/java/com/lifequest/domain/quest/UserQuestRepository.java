package com.lifequest.domain.quest;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserQuestRepository extends JpaRepository<UserQuest, Long> {
    Optional<UserQuest> findByUserIdAndQuestId(Long userId, Long questId);
}
