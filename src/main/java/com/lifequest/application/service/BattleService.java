package com.lifequest.application.service;

import com.lifequest.api.advice.ApiException;
import com.lifequest.api.advice.ErrorCode;
import com.lifequest.api.dto.response.BattleStatusResponse;
import com.lifequest.api.dto.response.QuestCompleteResponse;
import com.lifequest.domain.battle.BattleLog;
import com.lifequest.domain.battle.BattleLogRepository;
import com.lifequest.domain.battle.BattleProgress;
import com.lifequest.domain.battle.BattleProgressRepository;
import com.lifequest.domain.boss.BossClear;
import com.lifequest.domain.boss.BossClearRepository;
import com.lifequest.domain.hunting.MonsterKillCount;
import com.lifequest.domain.hunting.MonsterKillCountRepository;
import com.lifequest.domain.item.UserItem;
import com.lifequest.domain.item.UserItemRepository;
import com.lifequest.domain.monster.Monster;
import com.lifequest.domain.monster.Trait;
import com.lifequest.domain.quest.Quest;
import com.lifequest.domain.quest.QuestRepository;
import com.lifequest.domain.quest.QuestStatus;
import com.lifequest.domain.user.User;
import com.lifequest.domain.user.UserRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BattleService {

    private final UserRepository userRepository;
    private final BattleProgressRepository progressRepository;
    private final BossClearRepository bossClearRepository;
    private final MonsterKillCountRepository killCountRepository;
    private final QuestRepository questRepository;
    private final BattleLogRepository battleLogRepository;
    private final UserItemRepository userItemRepository;

    private final Random random = new Random();

    @Transactional
    public BattleStatusResponse getBattleStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        BattleProgress progress = progressRepository.findByUserId(userId).orElse(null);

        if (progress == null || !progress.isInBattle()) {
            return BattleStatusResponse.notInBattle(user.getCurrentHp(), user.getMaxHp());
        }

        applyPendingMonsterAttacks(user, progress);
        applyPendingQuestFailures(user);
        applyMonsterRegeneration(progress);

        if (user.isDead()) {
            applyDeathPenalty(user, progress);
            return BattleStatusResponse.notInBattle(user.getCurrentHp(), user.getMaxHp());
        }

        List<BattleLog> logs = battleLogRepository.findTop20ByUserIdOrderByCreatedAtDesc(userId);
        return BattleStatusResponse.inBattle(user, progress, calculateNextMonsterAttack(progress), logs);
    }

    @Transactional
    public QuestCompleteResponse attackMonster(Long userId, Quest quest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        BattleProgress progress = progressRepository.findByUserId(userId).orElse(null);

        if (progress == null || !progress.isInBattle()) {
            throw new ApiException(ErrorCode.NOT_IN_BATTLE);
        }

        applyPendingMonsterAttacks(user, progress);
        applyPendingQuestFailures(user);
        applyMonsterRegeneration(progress);

        if (user.isDead()) {
            applyDeathPenalty(user, progress);
            return QuestCompleteResponse.died();
        }

        Monster monster = progress.getMonster();
        int damage = calculatePlayerDamage(user, quest, monster);

        if (hasTrait(monster, Trait.RANDOM)) {
            double multiplier = 0.5 + (random.nextDouble() * 1.5);
            damage = (int) (damage * multiplier);
        }

        int reflectDamage = 0;
        if (hasTrait(monster, Trait.MIRROR)) {
            double reflectRate = getTraitValue(monster, Trait.MIRROR);
            if (reflectRate == 0) reflectRate = 0.2;
            reflectDamage = (int) (damage * reflectRate);
            user.takeDamage(reflectDamage);
            battleLogRepository.save(BattleLog.reflect(userId, reflectDamage, monster.getName()));
        }

        progress.damageMonster(damage);
        battleLogRepository.save(BattleLog.playerAttack(userId, damage, false, monster.getName()));

        if (progress.isMonsterDead()) {
            return handleMonsterDefeat(user, progress);
        }

        return QuestCompleteResponse.attacked(damage, false, progress.getMonsterCurrentHp());
    }

    @Transactional
    public void fleeBattle(Long userId) {
        BattleProgress progress = progressRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_IN_BATTLE));

        if (!progress.isInBattle()) {
            throw new ApiException(ErrorCode.NOT_IN_BATTLE);
        }

        if (progress.isBossBattle()) {
            throw new ApiException(ErrorCode.CANNOT_FLEE_BOSS);
        }

        progress.reset();
    }

    private void applyPendingMonsterAttacks(User user, BattleProgress progress) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastAttack = progress.getLastMonsterAttack();
        Monster monster = progress.getMonster();

        int intervalHours = monster.getAttackIntervalHours();
        long hoursPassed = ChronoUnit.HOURS.between(lastAttack, now);
        int attackCount = (int) (hoursPassed / intervalHours);

        if (attackCount > 0) {
            int damagePerAttack = calculateMonsterDamage(user, monster);
            int totalDamage = damagePerAttack * attackCount;

            if (hasTrait(monster, Trait.POISON)) {
                double poisonValue = getTraitValue(monster, Trait.POISON);
                if (poisonValue == 0) poisonValue = 2;
                totalDamage += (int) (poisonValue * hoursPassed);
            }

            user.takeDamage(totalDamage);
            battleLogRepository.save(BattleLog.monsterAttack(user.getId(), totalDamage, attackCount, monster.getName()));

            progress.setLastMonsterAttack(lastAttack.plusHours((long) attackCount * intervalHours));
            progress.setMonsterAttackCount(progress.getMonsterAttackCount() + attackCount);
        }
    }

    private void applyMonsterRegeneration(BattleProgress progress) {
        Monster monster = progress.getMonster();
        if (!hasTrait(monster, Trait.REGENERATE)) return;

        long hoursPassed = ChronoUnit.HOURS.between(progress.getStartedAt(), LocalDateTime.now());
        double regenRate = getTraitValue(monster, Trait.REGENERATE);
        if (regenRate == 0) regenRate = 0.05;

        int healAmount = (int) (progress.getMonsterMaxHp() * regenRate * hoursPassed);
        if (healAmount > 0) progress.healMonster(healAmount);
    }

    private void applyPendingQuestFailures(User user) {
        List<Quest> overdue = questRepository.findByUserIdAndStatusAndDeadlineBefore(
                user.getId(), QuestStatus.PENDING, LocalDateTime.now());

        int totalDamage = 0;
        for (Quest quest : overdue) {
            totalDamage += 10 + (quest.getDifficulty() * 5);
            quest.setStatus(QuestStatus.FAILED);
        }

        if (totalDamage > 0) {
            user.takeDamage(totalDamage);
            battleLogRepository.save(BattleLog.questFail(user.getId(), totalDamage, overdue.size()));
        }
    }

    private void applyDeathPenalty(User user, BattleProgress progress) {
        user.applyDeathPenalty();
        battleLogRepository.save(BattleLog.defeat(user.getId()));
        progress.reset();
    }


    private QuestCompleteResponse handleMonsterDefeat(User user, BattleProgress progress) {
        Monster monster = progress.getMonster();
        boolean isBoss = progress.isBossBattle();
        boolean isHunting = progress.isHunting();

        int xpMultiplier = isBoss ? 3 : 1;
        int goldMultiplier = isBoss ? 3 : 1;

        int xp = monster.getXpReward() * xpMultiplier;
        int totalLuk = getTotalStat(user, "LUK");
        double goldBonus = 1.0 + (totalLuk * 0.01);
        int gold = (int) (monster.getGoldReward() * goldMultiplier * goldBonus);

        user.addXp(xp);
        user.addGold(gold);
        boolean leveledUp = user.checkLevelUp();

        String droppedItem = checkItemDrop(user, monster);

        if (isBoss) {
            bossClearRepository.findByUserIdAndBossId(user.getId(), monster.getId())
                    .ifPresentOrElse(
                            BossClear::incrementClearCount,
                            () -> bossClearRepository.save(BossClear.create(user, monster))
                    );
            battleLogRepository.save(BattleLog.bossVictory(user.getId(), monster.getName(), xp, gold));
            progress.reset();
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
            } else {
                progress.reset();
            }
        }

        return QuestCompleteResponse.monsterDefeated(
                0, false, isHunting ? progress.getMonsterCurrentHp() : 0, 
                xp, gold, droppedItem, leveledUp, leveledUp ? user.getLevel() : null);
    }

    private int calculatePlayerDamage(User user, Quest quest, Monster monster) {
        int baseDamage = (int) (quest.getBaseDamage() * (1 + (quest.getDifficulty() - 1) * 0.3));
        int totalAtk = getTotalStat(user, "ATK");
        double atkBonus = 1.0 + (totalAtk * 0.01);

        int totalPen = getTotalStat(user, "PEN");
        int effectiveDef = Math.max(0, monster.getDef() - totalPen);
        double defReduction = effectiveDef / 100.0;

        return (int) (baseDamage * atkBonus * (1 - defReduction));
    }

    private int calculateMonsterDamage(User user, Monster monster) {
        int monsterAttack = monster.getAtk();
        int totalDef = getTotalStat(user, "DEF");
        double defReduction = Math.min(totalDef * 0.005, 0.7);
        return (int) (monsterAttack * (1 - defReduction));
    }

    private int getTotalStat(User user, String stat) {
        int baseStat = switch (stat.toUpperCase()) {
            case "ATK" -> user.getStats().getAtk();
            case "DEF" -> user.getStats().getDef();
            case "PEN" -> user.getStats().getPen();
            case "LUK" -> user.getStats().getLuk();
            default -> 0;
        };

        List<UserItem> equipped = userItemRepository.findByUserIdAndEquippedTrue(user.getId());
        int itemBonus = equipped.stream()
                .mapToInt(ui -> switch (stat.toUpperCase()) {
                    case "ATK" -> ui.getItem().getAtkBonus();
                    case "DEF" -> ui.getItem().getDefBonus();
                    case "PEN" -> ui.getItem().getPenBonus();
                    case "LUK" -> ui.getItem().getLukBonus();
                    default -> 0;
                }).sum();

        return baseStat + itemBonus;
    }

    private boolean hasTrait(Monster monster, Trait trait) {
        return monster.getTrait() == trait || monster.getSecondTrait() == trait;
    }

    private double getTraitValue(Monster monster, Trait trait) {
        if (monster.getTrait() == trait) return monster.getTraitValue();
        if (monster.getSecondTrait() == trait) return monster.getSecondTraitValue();
        return 0;
    }

    private String checkItemDrop(User user, Monster monster) {
        if (monster.getDropItemId() == null) return null;
        int totalLuk = getTotalStat(user, "LUK");
        double dropRate = monster.getDropRate() + (totalLuk * 0.005);
        if (random.nextDouble() < dropRate) return "아이템 드롭!";
        return null;
    }

    private LocalDateTime calculateNextMonsterAttack(BattleProgress progress) {
        Monster monster = progress.getMonster();
        return progress.getLastMonsterAttack().plusHours(monster.getAttackIntervalHours());
    }
}
