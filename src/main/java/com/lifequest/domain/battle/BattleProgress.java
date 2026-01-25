package com.lifequest.domain.battle;

import com.lifequest.domain.hunting.HuntingGround;
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
@Table(name = "battle_progress")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // 사냥터 (사냥 중일 때)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hunting_ground_id")
    private HuntingGround huntingGround;

    // 현재 싸우는 몬스터
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BattleType battleType = BattleType.NONE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BattleStatus status = BattleStatus.IDLE;

    public static BattleProgress create(User user) {
        BattleProgress bp = new BattleProgress();
        bp.user = user;
        return bp;
    }

    // 사냥터 입장
    public void startHunting(HuntingGround huntingGround) {
        this.huntingGround = huntingGround;
        this.monster = huntingGround.getMonster();
        this.monsterCurrentHp = monster.getHp();
        this.monsterMaxHp = monster.getHp();
        this.monsterAttackCount = 0;
        this.startedAt = LocalDateTime.now();
        this.lastMonsterAttack = LocalDateTime.now();
        this.battleType = BattleType.HUNTING;
        this.status = BattleStatus.IN_PROGRESS;
    }

    // 보스 소환
    public void startBossBattle(Monster boss) {
        this.huntingGround = null;
        this.monster = boss;
        this.monsterCurrentHp = boss.getHp();
        this.monsterMaxHp = boss.getHp();
        this.monsterAttackCount = 0;
        this.startedAt = LocalDateTime.now();
        this.lastMonsterAttack = LocalDateTime.now();
        this.battleType = BattleType.BOSS;
        this.status = BattleStatus.IN_PROGRESS;
    }

    // 사냥터 몬스터 리스폰
    public void respawnMonster() {
        if (this.huntingGround == null) return;
        this.monster = huntingGround.getMonster();
        this.monsterCurrentHp = monster.getHp();
        this.monsterMaxHp = monster.getHp();
        this.monsterAttackCount = 0;
        this.lastMonsterAttack = LocalDateTime.now();
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

    public void reset() {
        this.huntingGround = null;
        this.monster = null;
        this.monsterCurrentHp = 0;
        this.monsterMaxHp = 0;
        this.monsterAttackCount = 0;
        this.startedAt = null;
        this.lastMonsterAttack = null;
        this.battleType = BattleType.NONE;
        this.status = BattleStatus.IDLE;
    }

    public boolean isInBattle() {
        return this.status == BattleStatus.IN_PROGRESS;
    }

    public boolean isHunting() {
        return this.battleType == BattleType.HUNTING && isInBattle();
    }

    public boolean isBossBattle() {
        return this.battleType == BattleType.BOSS && isInBattle();
    }
}
