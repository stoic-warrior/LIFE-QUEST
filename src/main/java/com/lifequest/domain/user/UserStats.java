package com.lifequest.domain.user;

import com.lifequest.domain.quest.StatType;
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
@Table(name = "user_stats")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStats {
    @Id
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private int strength = 1;

    @Column(nullable = false)
    private int intelligence = 1;

    @Column(nullable = false)
    private int creativity = 1;

    @Column(nullable = false)
    private int social = 1;

    @Column(nullable = false)
    private int emotional = 1;

    @Column(nullable = false)
    private int spiritual = 1;

    public static UserStats create() {
        return new UserStats();
    }

    public void addStat(StatType statType, int amount) {
        switch (statType) {
            case STRENGTH -> strength += amount;
            case INTELLIGENCE -> intelligence += amount;
            case CREATIVITY -> creativity += amount;
            case SOCIAL -> social += amount;
            case EMOTIONAL -> emotional += amount;
            case SPIRITUAL -> spiritual += amount;
        }
    }
}
