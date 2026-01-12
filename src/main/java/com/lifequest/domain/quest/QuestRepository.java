package com.lifequest.domain.quest;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestRepository extends JpaRepository<Quest, Long> {
    List<Quest> findByType(QuestType type);
}
