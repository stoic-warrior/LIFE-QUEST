package com.lifequest.domain.quest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestRepository extends JpaRepository<Quest, Long> {
    List<Quest> findByUserId(Long userId);
    List<Quest> findByUserIdAndStatus(Long userId, QuestStatus status);
    List<Quest> findByUserIdAndStatusAndDeadlineBefore(Long userId, QuestStatus status, LocalDateTime deadline);
    Optional<Quest> findByIdAndUserId(Long id, Long userId);
}
