package com.lifequest.api.dto.response;

import com.lifequest.domain.battle.BattleLog;
import java.time.LocalDateTime;

public record BattleLogResponse(
        String type,
        int damage,
        String description,
        LocalDateTime timestamp
) {
    public static BattleLogResponse from(BattleLog log) {
        return new BattleLogResponse(
                log.getLogType().name(),
                log.getDamage(),
                log.getDescription(),
                log.getCreatedAt()
        );
    }
}
