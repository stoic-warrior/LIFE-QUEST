package com.lifequest.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "player_stats")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlayerStats {
    @Id
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private int atk = 10;  // 공격력

    @Column(nullable = false)
    private int def = 10;  // 방어력

    @Column(nullable = false)
    private int pen = 10;  // 방어관통

    @Column(nullable = false)
    private int luk = 10;  // 행운

    @Column(nullable = false)
    private int statPoints = 0;  // 미사용 스탯 포인트

    public static PlayerStats create() {
        return new PlayerStats();
    }

    public void addStat(String statType, int amount) {
        switch (statType.toUpperCase()) {
            case "ATK" -> atk += amount;
            case "DEF" -> def += amount;
            case "PEN" -> pen += amount;
            case "LUK" -> luk += amount;
            default -> throw new IllegalArgumentException("Invalid stat type: " + statType);
        }
    }

    public int getStat(String statType) {
        return switch (statType.toUpperCase()) {
            case "ATK" -> atk;
            case "DEF" -> def;
            case "PEN" -> pen;
            case "LUK" -> luk;
            default -> throw new IllegalArgumentException("Invalid stat type: " + statType);
        };
    }

    public void addStatPoints(int points) {
        this.statPoints += points;
    }
}
