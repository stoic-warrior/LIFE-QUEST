package com.lifequest.api.dto.response;

import com.lifequest.domain.battle.BattleLog;
import com.lifequest.domain.dungeon.DungeonProgress;
import com.lifequest.domain.user.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record BattleStatusResponse(
        boolean inBattle,
        DungeonResponse dungeon,
        MonsterResponse monster,
        LocalDateTime nextMonsterAttack,
        int playerCurrentHp,
        int playerMaxHp,
        List<BattleLogResponse> recentLogs
) {
    public static BattleStatusResponse notInBattle(int currentHp, int maxHp) {
        return new BattleStatusResponse(false, null, null, null, currentHp, maxHp, List.of());
    }

    public static BattleStatusResponse inBattle(User user, DungeonProgress progress,
                                                 LocalDateTime nextAttack, List<BattleLog> logs) {
        return new BattleStatusResponse(
                true,
                DungeonResponse.from(progress.getDungeon(), true),
                MonsterResponse.fromProgress(progress),
                nextAttack,
                user.getCurrentHp(),
                user.getMaxHp(),
                logs.stream().map(BattleLogResponse::from).collect(Collectors.toList())
        );
    }
}
