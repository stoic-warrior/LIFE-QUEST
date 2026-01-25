package com.lifequest.domain.boss;

import com.lifequest.domain.monster.Monster;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "boss_summons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BossSummon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // "슬라임 킹 소환서"

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "boss_id", nullable = false)
    private Monster boss;

    @Column(nullable = false)
    private int price; // 골드 가격

    // 해금 조건: 이 몬스터를 N마리 처치
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "required_monster_id", nullable = false)
    private Monster requiredMonster;

    @Column(nullable = false)
    private int requiredKillCount;

    @Column(length = 255)
    private String description;

    public static BossSummon create(String name, Monster boss, int price, 
                                     Monster requiredMonster, int requiredKillCount, String description) {
        BossSummon bs = new BossSummon();
        bs.name = name;
        bs.boss = boss;
        bs.price = price;
        bs.requiredMonster = requiredMonster;
        bs.requiredKillCount = requiredKillCount;
        bs.description = description;
        return bs;
    }
}
