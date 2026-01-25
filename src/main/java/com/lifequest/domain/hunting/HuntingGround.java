package com.lifequest.domain.hunting;

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
@Table(name = "hunting_grounds")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HuntingGround {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "monster_id", nullable = false)
    private Monster monster;

    @Column(nullable = false)
    private int requiredLevel = 1;

    // 해금 조건: 이 보스를 클리어해야 해금 (null이면 기본 해금)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unlock_boss_id")
    private Monster unlockBoss;

    @Column(length = 255)
    private String description;

    private String imageUrl;

    public static HuntingGround create(String name, Monster monster, int requiredLevel, 
                                        Monster unlockBoss, String description) {
        HuntingGround hg = new HuntingGround();
        hg.name = name;
        hg.monster = monster;
        hg.requiredLevel = requiredLevel;
        hg.unlockBoss = unlockBoss;
        hg.description = description;
        return hg;
    }
}
