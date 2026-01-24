package com.lifequest.application.service;

import com.lifequest.api.advice.ApiException;
import com.lifequest.api.advice.ErrorCode;
import com.lifequest.api.dto.response.BattleLogResponse;
import com.lifequest.api.dto.response.BattleStatusResponse;
import com.lifequest.api.dto.response.QuestCompleteResponse;
import com.lifequest.domain.battle.BattleLog;
import com.lifequest.domain.battle.BattleLogRepository;
import com.lifequest.domain.dungeon.Dungeon;
import com.lifequest.domain.dungeon.DungeonProgress;
import com.lifequest.domain.dungeon.DungeonProgressRepository;
import com.lifequest.domain.dungeon.EffectType;
import com.lifequest.domain.dungeon.ProgressStatus;
import com.lifequest.domain.item.UserItem;
import com.lifequest.domain.item.UserItemRepository;
import com.lifequest.domain.monster.Monster;
import com.lifequest.domain.monster.Trait;
import com.lifequest.domain.quest.Quest;
import com.lifequest.domain.quest.QuestRepository;
import com.lifequest.domain.quest.QuestStatus;
import com.lifequest.domain.user.PlayerStats;
import com.lifequest.domain.user.User;
import com.lifequest.domain.user.UserRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BattleService {

    private final UserRepository userRepository;
    private final DungeonProgressRepository progressRepository;
    private final QuestRepository questRepository;
    private final BattleLogRepository battleLogRepository;
    private final UserItemRepository userItemRepository;

    @Transactional
    public BattleStatusResponse getBattleStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        DungeonProgress progress = progressRepository.findByUserId(userId)
                .orElse(null);

        if (progress == null || !progress.isInBattle()) {
            return BattleStatusResponse.notInBattle(user.getCurrentHp(), user.getMaxHp());
        }

        // 지연 계산: 밀린 데미지 모두 적용
        applyPendingMonsterAttacks(user, progress);
        applyPendingQuestFailures(user);
        applyEnvironmentDamage(user, progress);

        // 사망 체크
        if (user.isDead()) {
            applyDeathPenalty(user, progress);
        }

        List<BattleLog> logs = battleLogRepository.findTop20ByUserIdOrderByCreatedAtDesc(userId);
        return BattleStatusResponse.inBattle(user, progress, calculateNextMonsterAttack(progress), logs);
    }

    @Transactional
    public QuestCompleteResponse attackMonster(Long userId, Quest quest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        DungeonProgress progress = progressRepository.findByUserId(userId)
                .orElse(null);

        if (progress == null || !progress.isInBattle()) {
            throw new ApiException(ErrorCode.NOT_IN_BATTLE);
        }

        // 먼저 밀린 데미지 적용
        applyPendingMonsterAttacks(user, progress);
        applyPendingQuestFailures(user);
        applyEnvironmentDamage(user, progress);

        if (user.isDead()) {
            applyDeathPenalty(user, progress);
            return QuestCompleteResponse.died();
        }

        // 데미지 계산
        int damage = calculatePlayerDamage(user, quest, progress.getMonster(), progress.getDungeon());
        boolean isCritical = checkCritical(user, progress.getMonster());
        if (isCritical) {
            int totalCrt = getTotalStat(user, "CRT");
            damage = (int) (damage * (1.5 + totalCrt * 0.01));
        }

        // 몬스터에게 데미지
        progress.damageMonster(damage);
        battleLogRepository.save(BattleLog.playerAttack(userId, damage, isCritical, progress.getMonster().getName()));

        // 몬스터 처치 확인
        if (progress.isMonsterDead()) {
            return handleMonsterDefeat(user, progress);
        }

        return QuestCompleteResponse.attacked(damage, isCritical, progress.getMonsterCurrentHp());
    }

    private void applyPendingMonsterAttacks(User user, DungeonProgress progress) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastAttack = progress.getLastMonsterAttack();
        Monster monster = progress.getMonster();
        Dungeon dungeon = progress.getDungeon();

        int intervalHours = monster.getAttackIntervalHours();

        // ACCELERATE 특성: 공격 횟수에 따라 주기 감소
        if (monster.getTrait() == Trait.ACCELERATE) {
            double reduction = progress.getMonsterAttackCount() * monster.getTraitValue();
            intervalHours = Math.max(1, (int) (intervalHours * (1 - Math.min(reduction, 0.5))));
        }

        // GLACIER 던전: 주기 증가
        if (dungeon.getEffectType() == EffectType.SLOW_MONSTER) {
            intervalHours = (int) (intervalHours * (1 + dungeon.getEffectValue()));
        }

        long hoursPassed = ChronoUnit.HOURS.between(lastAttack, now);
        int attackCount = (int) (hoursPassed / intervalHours);

        if (attackCount > 0) {
            int damagePerAttack = calculateMonsterDamage(user, monster);
            int totalDamage = damagePerAttack * attackCount;

            // LIFESTEAL 특성: 몬스터 회복
            if (monster.getTrait() == Trait.LIFESTEAL) {
                int heal = (int) (totalDamage * monster.getTraitValue());
                progress.healMonster(heal);
            }

            user.takeDamage(totalDamage);
            battleLogRepository.save(BattleLog.monsterAttack(user.getId(), totalDamage, attackCount, monster.getName()));

            progress.setLastMonsterAttack(lastAttack.plusHours((long) attackCount * intervalHours));
            progress.setMonsterAttackCount(progress.getMonsterAttackCount() + attackCount);
        }
    }

    private void applyPendingQuestFailures(User user) {
        List<Quest> overdue = questRepository.findByUserIdAndStatusAndDeadlineBefore(
                user.getId(), QuestStatus.PENDING, LocalDateTime.now());

        int totalDamage = 0;
        for (Quest quest : overdue) {
            totalDamage += calculateQuestFailDamage(quest);
            quest.setStatus(QuestStatus.FAILED);
        }

        if (totalDamage > 0) {
            user.takeDamage(totalDamage);
            battleLogRepository.save(BattleLog.questFail(user.getId(), totalDamage, overdue.size()));
        }
    }

    private void applyEnvironmentDamage(User user, DungeonProgress progress) {
        Dungeon dungeon = progress.getDungeon();
        if (dungeon.getEffectType() != EffectType.DOT_DAMAGE) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        long hours = ChronoUnit.HOURS.between(progress.getLastEnvironmentCalc(), now);

        if (hours > 0) {
            int damage = (int) (dungeon.getEffectValue() * hours);
            user.takeDamage(damage);
            battleLogRepository.save(BattleLog.environment(user.getId(), damage, dungeon.getName()));
            progress.setLastEnvironmentCalc(now);
        }
    }

    private void applyDeathPenalty(User user, DungeonProgress progress) {
        user.applyDeathPenalty();
        progress.defeat();
        progress.reset();
        battleLogRepository.save(BattleLog.defeat(user.getId()));
    }

    private QuestCompleteResponse handleMonsterDefeat(User user, DungeonProgress progress) {
        Monster monster = progress.getMonster();

        // 보상 계산
        int xp = monster.getXpReward();
        int totalLuk = getTotalStat(user, "LUK");
        double goldBonus = 1.0 + (totalLuk * 0.01);
        int gold = (int) (monster.getGoldReward() * goldBonus);

        // 보상 지급
        user.addXp(xp);
        user.addGold(gold);
        boolean leveledUp = user.checkLevelUp();

        // 아이템 드롭 체크
        String droppedItem = checkItemDrop(user, monster);

        // 로그 기록
        battleLogRepository.save(BattleLog.victory(user.getId(), monster.getName(), xp, gold));

        // 던전 클리어
        progress.victory();
        progress.reset();

        return QuestCompleteResponse.monsterDefeated(
                0, false, 0, xp, gold, droppedItem, leveledUp, leveledUp ? user.getLevel() : null);
    }

    private int calculatePlayerDamage(User user, Quest quest, Monster monster, Dungeon dungeon) {
        // 기본 데미지
        int baseDamage = (int) (quest.getBaseDamage() * (1 + (quest.getDifficulty() - 1) * 0.3));

        // ATK 보너스
        int totalAtk = getTotalStat(user, "ATK");
        double atkBonus = 1.0 + (totalAtk * 0.01);

        // 몬스터 방어력 (ARMOR 특성)
        double monsterArmor = monster.getTrait() == Trait.ARMOR ? monster.getTraitValue() : 0;

        // 던전 환경 디버프
        double envDebuff = dungeon.getEffectType() == EffectType.ATK_DEBUFF ? dungeon.getEffectValue() : 0;

        return (int) (baseDamage * atkBonus * (1 - monsterArmor) * (1 - envDebuff));
    }

    private boolean checkCritical(User user, Monster monster) {
        if (monster.getTrait() == Trait.CRIT_IMMUNE) {
            return false;
        }
        int totalCrt = getTotalStat(user, "CRT");
        double critChance = totalCrt * 0.005;
        return Math.random() < critChance;
    }

    private int calculateMonsterDamage(User user, Monster monster) {
        int monsterAttack = monster.getAttack();
        int totalDef = getTotalStat(user, "DEF");
        double defReduction = Math.min(totalDef * 0.01, 0.7);
        return (int) (monsterAttack * (1 - defReduction));
    }

    private int calculateQuestFailDamage(Quest quest) {
        return 10 + (quest.getDifficulty() * 5);
    }

    private int getTotalStat(User user, String stat) {
        PlayerStats stats = user.getStats();
        int baseStat = stats != null ? stats.getStat(stat) : 10;
        int equipBonus = getEquipmentBonus(user.getId(), stat);
        return baseStat + equipBonus;
    }

    private int getEquipmentBonus(Long userId, String stat) {
        List<UserItem> equipped = userItemRepository.findByUserIdAndEquippedTrue(userId);
        return equipped.stream()
                .mapToInt(ui -> switch (stat.toUpperCase()) {
                    case "ATK" -> ui.getItem().getAtkBonus();
                    case "DEF" -> ui.getItem().getDefBonus();
                    case "CRT" -> ui.getItem().getCrtBonus();
                    case "LUK" -> ui.getItem().getLukBonus();
                    default -> 0;
                })
                .sum();
    }

    private String checkItemDrop(User user, Monster monster) {
        if (monster.getDropItemId() == null) {
            return null;
        }
        int totalLuk = getTotalStat(user, "LUK");
        double dropRate = monster.getDropRate() + (totalLuk * 0.005);
        if (Math.random() < dropRate) {
            // TODO: 실제 아이템 지급 로직
            return "아이템 드롭!";
        }
        return null;
    }

    private LocalDateTime calculateNextMonsterAttack(DungeonProgress progress) {
        Monster monster = progress.getMonster();
        Dungeon dungeon = progress.getDungeon();

        int intervalHours = monster.getAttackIntervalHours();

        if (monster.getTrait() == Trait.ACCELERATE) {
            double reduction = progress.getMonsterAttackCount() * monster.getTraitValue();
            intervalHours = Math.max(1, (int) (intervalHours * (1 - Math.min(reduction, 0.5))));
        }

        if (dungeon.getEffectType() == EffectType.SLOW_MONSTER) {
            intervalHours = (int) (intervalHours * (1 + dungeon.getEffectValue()));
        }

        return progress.getLastMonsterAttack().plusHours(intervalHours);
    }
}
