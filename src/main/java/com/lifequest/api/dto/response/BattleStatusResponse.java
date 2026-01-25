package com.lifequest.api.dto.response;

import com.lifequest.domain.battle.BattleLog;
import com.lifequest.domain.battle.BattleProgress;
import com.lifequest.domain.user.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record BattleStatusResponse(
        boolean inBattle,
        String battleType,  // HUNTING, BOSS, null
        HuntingGroundResponse huntingGround,
        MonsterResponse monster,
        LocalDateTime nextMonsterAttack,
        int playerCurrentHp,
        int playerMaxHp,
        List<BattleLogResponse> recentLogs
) {
    public static BattleStatusResponse notInBattle(int currentHp, int maxHp) {
        return new BattleStatusResponse(false, null, null, null, null, currentHp, maxHp, List.of());
    }

    public static BattleStatusResponse inBattle(User user, BattleProgress progress,
                                                 LocalDateTime nextAttack, List<BattleLog> logs) {
        HuntingGroundResponse hgResponse = null;
        if (progress.getHuntingGround() != null) {
            hgResponse = HuntingGroundResponse.from(progress.getHuntingGround(), true, 0);
        }

        return new BattleStatusResponse(
                true,
                progress.getBattleType().name(),
                hgResponse,
                MonsterResponse.fromProgress(progress),
                nextAttack,
                user.getCurrentHp(),
                user.getMaxHp(),
                logs.stream().map(BattleLogResponse::from).collect(Collectors.toList())
        );
    }
}
