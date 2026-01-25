package com.lifequest.domain.boss;

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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "boss_clears", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "boss_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BossClear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boss_id", nullable = false)
    private Monster boss;

    @Column(nullable = false)
    private LocalDateTime firstClearedAt;

    @Column(nullable = false)
    private int clearCount = 1;

    public static BossClear create(User user, Monster boss) {
        BossClear bc = new BossClear();
        bc.user = user;
        bc.boss = boss;
        bc.firstClearedAt = LocalDateTime.now();
        bc.clearCount = 1;
        return bc;
    }

    public void incrementClearCount() {
        this.clearCount++;
    }
}
