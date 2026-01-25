package com.lifequest.application.service;

import com.lifequest.api.advice.ApiException;
import com.lifequest.api.advice.ErrorCode;
import com.lifequest.domain.battle.BattleLog;
import com.lifequest.domain.battle.BattleLogRepository;
import com.lifequest.domain.battle.BattleProgress;
import com.lifequest.domain.battle.BattleProgressRepository;
import com.lifequest.domain.boss.BossClear;
import com.lifequest.domain.boss.BossClearRepository;
import com.lifequest.domain.hunting.MonsterKillCount;
import com.lifequest.domain.hunting.MonsterKillCountRepository;
import com.lifequest.domain.monster.Monster;
import com.lifequest.domain.user.User;
import com.lifequest.domain.user.UserRepository;
import com.lifequest.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final BattleProgressRepository battleProgressRepository;
    private final BattleLogRepository battleLogRepository;
    private final BossClearRepository bossClearRepository;
    private final MonsterKillCountRepository killCountRepository;

    private static final int ADMIN_DAMAGE = 500;
    private static final int GOLD_AMOUNT = 10000;

    @Transactional
    public String instantAttack(Long userId) {
        User user = getAdminUser(userId);

        BattleProgress progress = battleProgressRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_IN_BATTLE));

        if (!progress.isInBattle()) {
            throw new ApiException(ErrorCode.NOT_IN_BATTLE);
        }

        Monster monster = progress.getMonster();
        progress.damageMonster(ADMIN_DAMAGE);
        battleLogRepository.save(BattleLog.playerAttack(userId, ADMIN_DAMAGE, false, monster.getName()));

        // 몬스터 처치 확인
        if (progress.isMonsterDead()) {
            return handleMonsterDefeat(user, progress);
        }

        return String.format("%s에게 %d 데미지! (남은 HP: %d)", 
                monster.getName(), ADMIN_DAMAGE, progress.getMonsterCurrentHp());
    }

    @Transactional
    public String instantLevelUp(Long userId) {
        User user = getAdminUser(userId);
        
        int oldLevel = user.getLevel();
        user.setLevel(oldLevel + 1);
        user.setStatPoints(user.getStatPoints() + 3);
        user.setMaxHp(user.getMaxHp() + 10);
        user.setCurrentHp(user.getMaxHp());

        return String.format("레벨업! %d → %d (스탯포인트 +3)", oldLevel, user.getLevel());
    }

    @Transactional
    public String addGold(Long userId) {
        User user = getAdminUser(userId);
        
        long oldGold = user.getGold();
        user.addGold(GOLD_AMOUNT);

        return String.format("골드 추가! %d → %d (+%d)", oldGold, user.getGold(), GOLD_AMOUNT);
    }

    private User getAdminUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() != UserRole.ADMIN) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        return user;
    }

    private String handleMonsterDefeat(User user, BattleProgress progress) {
        Monster monster = progress.getMonster();
        boolean isBoss = progress.isBossBattle();
        boolean isHunting = progress.isHunting();

        int xpMultiplier = isBoss ? 3 : 1;
        int goldMultiplier = isBoss ? 3 : 1;

        int xp = monster.getXpReward() * xpMultiplier;
        int gold = monster.getGoldReward() * goldMultiplier;

        user.addXp(xp);
        user.addGold(gold);
        boolean leveledUp = user.checkLevelUp();

        String result;
        if (isBoss) {
            bossClearRepository.findByUserIdAndBossId(user.getId(), monster.getId())
                    .ifPresentOrElse(
                            BossClear::incrementClearCount,
                            () -> bossClearRepository.save(BossClear.create(user, monster))
                    );
            battleLogRepository.save(BattleLog.bossVictory(user.getId(), monster.getName(), xp, gold));
            progress.reset();
            result = String.format("🏆 %s 처치! +%dXP, +%dG", monster.getName(), xp, gold);
        } else {
            killCountRepository.findByUserIdAndMonsterId(user.getId(), monster.getId())
                    .ifPresentOrElse(
                            MonsterKillCount::increment,
                            () -> {
                                MonsterKillCount mkc = MonsterKillCount.create(user, monster);
                                mkc.increment();
                                killCountRepository.save(mkc);
                            }
                    );
            battleLogRepository.save(BattleLog.victory(user.getId(), monster.getName(), xp, gold));

            if (isHunting) {
                progress.respawnMonster();
                result = String.format("⚔️ %s 처치! +%dXP, +%dG (리스폰)", monster.getName(), xp, gold);
            } else {
                progress.reset();
                result = String.format("⚔️ %s 처치! +%dXP, +%dG", monster.getName(), xp, gold);
            }
        }

        if (leveledUp) {
            result += String.format(" 🎉 레벨업! Lv.%d", user.getLevel());
        }

        return result;
    }
}
