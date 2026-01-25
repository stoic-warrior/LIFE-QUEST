package com.lifequest.domain.hunting;

import com.lifequest.domain.monster.Monster;
import com.lifequest.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "monster_kill_counts", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "monster_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonsterKillCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monster_id", nullable = false)
    private Monster monster;

    @Column(nullable = false)
    private int killCount = 0;

    public static MonsterKillCount create(User user, Monster monster) {
        MonsterKillCount mkc = new MonsterKillCount();
        mkc.user = user;
        mkc.monster = monster;
        mkc.killCount = 0;
        return mkc;
    }

    public void increment() {
        this.killCount++;
    }
}
