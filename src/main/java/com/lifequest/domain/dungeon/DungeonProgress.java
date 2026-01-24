package com.lifequest.domain.dungeon;

import com.lifequest.domain.monster.Monster;
import com.lifequest.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dungeon_progress")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DungeonProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dungeon_id")
    private Dungeon dungeon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monster_id")
    private Monster monster;

    @Column(nullable = false)
    private int monsterCurrentHp = 0;

    @Column(nullable = false)
    private int monsterMaxHp = 0;

    @Column(nullable = false)
    private int monsterAttackCount = 0;

    private LocalDateTime startedAt;

    private LocalDateTime lastMonsterAttack;

    private LocalDateTime lastEnvironmentCalc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgressStatus status = ProgressStatus.IDLE;

    public static DungeonProgress create(User user) {
        DungeonProgress progress = new DungeonProgress();
        progress.user = user;
        return progress;
    }

    public void startDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
        this.monster = dungeon.getMonster();
        this.monsterCurrentHp = monster.getHp();
        this.monsterMaxHp = monster.getHp();
        this.monsterAttackCount = 0;
        this.startedAt = LocalDateTime.now();
        this.lastMonsterAttack = LocalDateTime.now();
        this.lastEnvironmentCalc = LocalDateTime.now();
        this.status = ProgressStatus.IN_PROGRESS;
    }

    public void damageMonster(int damage) {
        this.monsterCurrentHp = Math.max(0, this.monsterCurrentHp - damage);
    }

    public void healMonster(int amount) {
        this.monsterCurrentHp = Math.min(this.monsterMaxHp, this.monsterCurrentHp + amount);
    }

    public boolean isMonsterDead() {
        return this.monsterCurrentHp <= 0;
    }

    public void victory() {
        this.status = ProgressStatus.VICTORY;
    }

    public void defeat() {
        this.status = ProgressStatus.DEFEAT;
    }

    public void reset() {
        this.dungeon = null;
        this.monster = null;
        this.monsterCurrentHp = 0;
        this.monsterMaxHp = 0;
        this.monsterAttackCount = 0;
        this.startedAt = null;
        this.lastMonsterAttack = null;
        this.lastEnvironmentCalc = null;
        this.status = ProgressStatus.IDLE;
    }

    public boolean isInBattle() {
        return this.status == ProgressStatus.IN_PROGRESS;
    }
}
