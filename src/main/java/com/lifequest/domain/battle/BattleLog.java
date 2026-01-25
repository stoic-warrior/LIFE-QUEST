package com.lifequest.domain.battle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "battle_logs")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogType logType;

    private int damage;

    private String description;

    private LocalDateTime createdAt = LocalDateTime.now();

    public static BattleLog playerAttack(Long userId, int damage, boolean isCritical, String monsterName) {
        BattleLog log = new BattleLog();
        log.userId = userId;
        log.logType = LogType.PLAYER_ATTACK;
        log.damage = damage;
        log.description = String.format("%s에게 %d 데미지!", monsterName, damage);
        return log;
    }

    public static BattleLog monsterAttack(Long userId, int damage, int attackCount, String monsterName) {
        BattleLog log = new BattleLog();
        log.userId = userId;
        log.logType = LogType.MONSTER_ATTACK;
        log.damage = damage;
        log.description = String.format("%s의 공격! %d 데미지 (%d회)", monsterName, damage, attackCount);
        return log;
    }

    public static BattleLog reflect(Long userId, int damage, String monsterName) {
        BattleLog log = new BattleLog();
        log.userId = userId;
        log.logType = LogType.REFLECT;
        log.damage = damage;
        log.description = String.format("%s의 반사! %d 데미지 받음", monsterName, damage);
        return log;
    }

    public static BattleLog questFail(Long userId, int damage, int questCount) {
        BattleLog log = new BattleLog();
        log.userId = userId;
        log.logType = LogType.QUEST_FAIL;
        log.damage = damage;
        log.description = String.format("퀘스트 %d개 실패! %d 데미지", questCount, damage);
        return log;
    }

    public static BattleLog victory(Long userId, String monsterName, int xp, int gold) {
        BattleLog log = new BattleLog();
        log.userId = userId;
        log.logType = LogType.VICTORY;
        log.damage = 0;
        log.description = String.format("%s 처치! +%d XP, +%d 골드", monsterName, xp, gold);
        return log;
    }

    public static BattleLog bossVictory(Long userId, String bossName, int xp, int gold) {
        BattleLog log = new BattleLog();
        log.userId = userId;
        log.logType = LogType.BOSS_VICTORY;
        log.damage = 0;
        log.description = String.format("🏆 %s 처치! +%d XP, +%d 골드", bossName, xp, gold);
        return log;
    }

    public static BattleLog defeat(Long userId) {
        BattleLog log = new BattleLog();
        log.userId = userId;
        log.logType = LogType.DEFEAT;
        log.damage = 0;
        log.description = "쓰러졌습니다... 경험치 -10%, 골드 -20%";
        return log;
    }
}
