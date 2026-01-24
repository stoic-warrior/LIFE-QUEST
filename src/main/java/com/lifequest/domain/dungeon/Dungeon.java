package com.lifequest.domain.dungeon;

import com.lifequest.domain.monster.Monster;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dungeons")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dungeon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int floorNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Environment environment = Environment.PLAIN;

    @Column(length = 255)
    private String environmentEffect;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EffectType effectType = EffectType.NONE;

    @Column(nullable = false)
    private double effectValue = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "monster_id")
    private Monster monster;

    @Column(nullable = false)
    private int requiredLevel = 1;

    public static Dungeon create(String name, int floorNumber, Environment environment,
                                  String environmentEffect, EffectType effectType, double effectValue,
                                  Monster monster, int requiredLevel) {
        Dungeon dungeon = new Dungeon();
        dungeon.name = name;
        dungeon.floorNumber = floorNumber;
        dungeon.environment = environment;
        dungeon.environmentEffect = environmentEffect;
        dungeon.effectType = effectType;
        dungeon.effectValue = effectValue;
        dungeon.monster = monster;
        dungeon.requiredLevel = requiredLevel;
        return dungeon;
    }
}
