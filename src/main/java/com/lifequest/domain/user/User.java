package com.lifequest.domain.user;

import com.lifequest.domain.guild.Guild;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int level = 1;

    @Column(nullable = false)
    private long currentXp = 0;

    @Column(nullable = false)
    private long totalXp = 0;

    @Column(nullable = false)
    private long gold = 0;

    @Column(nullable = false)
    private int statPoints = 0;

    @Column(nullable = false)
    private int streakDays = 0;

    private LocalDate lastLoginDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_id")
    private Guild guild;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserStats stats;

    public static User create(String email, String nickname, String encodedPassword, UUID uuid) {
        User user = new User();
        user.email = email;
        user.nickname = nickname;
        user.password = encodedPassword;
        user.uuid = uuid;
        return user;
    }

    public void initializeStats(UserStats stats) {
        this.stats = stats;
        stats.setUser(this);
    }

    public void addXp(long xp) {
        this.currentXp += xp;
        this.totalXp += xp;
    }
}
