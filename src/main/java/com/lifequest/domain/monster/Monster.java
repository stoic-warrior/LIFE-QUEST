package com.lifequest.domain.monster;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "monsters")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Monster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int hp;

    @Column(nullable = false)
    private int atk;

    @Column(nullable = false)
    private int def; // 방어력 (%, 0~100)

    @Column(nullable = false)
    private int attackIntervalHours;

    @Column(nullable = false)
    private int xpReward;

    @Column(nullable = false)
    private int goldReward;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Trait trait = Trait.NONE;

    @Column(nullable = false)
    private double traitValue = 0;

    // 보스는 BOSS 특성 + 추가 특성 가능
    @Enumerated(EnumType.STRING)
    private Trait secondTrait;

    @Column(nullable = false)
    private double secondTraitValue = 0;

    @Column(nullable = false)
    private double dropRate = 0.1;

    private Long dropItemId;

    private String imageUrl;

    @Column(nullable = false)
    private boolean isBoss = false;

    public static Monster create(String name, int hp, int atk, int def, int attackIntervalHours,
                                  int xpReward, int goldReward, Trait trait, double traitValue, double dropRate) {
        Monster monster = new Monster();
        monster.name = name;
        monster.hp = hp;
        monster.atk = atk;
        monster.def = def;
        monster.attackIntervalHours = attackIntervalHours;
        monster.xpReward = xpReward;
        monster.goldReward = goldReward;
        monster.trait = trait;
        monster.traitValue = traitValue;
        monster.dropRate = dropRate;
        return monster;
    }

    public static Monster createBoss(String name, int hp, int atk, int def, int attackIntervalHours,
                                      int xpReward, int goldReward, 
                                      Trait secondTrait, double secondTraitValue,
                                      double dropRate, Long dropItemId) {
        Monster monster = new Monster();
        monster.name = name;
        monster.hp = hp;
        monster.atk = atk;
        monster.def = def;
        monster.attackIntervalHours = attackIntervalHours;
        monster.xpReward = xpReward;
        monster.goldReward = goldReward;
        monster.trait = Trait.BOSS;
        monster.traitValue = 0;
        monster.secondTrait = secondTrait;
        monster.secondTraitValue = secondTraitValue;
        monster.dropRate = dropRate;
        monster.dropItemId = dropItemId;
        monster.isBoss = true;
        return monster;
    }

    public int getAttack() {
        return this.atk;
    }
}
