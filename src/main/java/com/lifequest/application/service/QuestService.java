package com.lifequest.application.service;

import com.lifequest.api.advice.ApiException;
import com.lifequest.api.advice.ErrorCode;
import com.lifequest.api.dto.request.QuestCreateRequest;
import com.lifequest.api.dto.response.QuestCompletionResponse;
import com.lifequest.domain.quest.Quest;
import com.lifequest.domain.quest.QuestRepository;
import com.lifequest.domain.quest.QuestStatus;
import com.lifequest.domain.quest.QuestType;
import com.lifequest.domain.quest.UserQuest;
import com.lifequest.domain.quest.UserQuestRepository;
import com.lifequest.domain.user.User;
import com.lifequest.domain.user.UserStats;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestService {
    private final QuestRepository questRepository;
    private final UserQuestRepository userQuestRepository;
    private final UserService userService;

    public QuestService(QuestRepository questRepository,
                        UserQuestRepository userQuestRepository,
                        UserService userService) {
        this.questRepository = questRepository;
        this.userQuestRepository = userQuestRepository;
        this.userService = userService;
    }

    public List<Quest> list(QuestType type) {
        if (type == null) {
            return questRepository.findAll();
        }
        return questRepository.findByType(type);
    }

    public Quest get(Long questId) {
        return questRepository.findById(questId)
            .orElseThrow(() -> new ApiException(ErrorCode.QUEST_NOT_FOUND, "Quest not found"));
    }

    @Transactional
    public Quest create(Long userId, QuestCreateRequest request) {
        User creator = userService.getUser(userId);
        Quest quest = Quest.create(
            request.getTitle(),
            request.getDescription(),
            request.getType(),
            request.getDifficulty(),
            request.getBaseXp(),
            request.getGoldReward(),
            request.getTargetStat(),
            request.isRepeatable(),
            true,
            creator
        );
        return questRepository.save(quest);
    }

    @Transactional
    public UserQuest accept(Long userId, Long questId) {
        User user = userService.getUser(userId);
        Quest quest = get(questId);
        return userQuestRepository.findByUserIdAndQuestId(userId, questId)
            .orElseGet(() -> {
                UserQuest userQuest = UserQuest.create(
                    user,
                    quest,
                    QuestStatus.IN_PROGRESS,
                    LocalDateTime.now()
                );
                return userQuestRepository.save(userQuest);
            });
    }

    @Transactional
    public QuestCompletionResponse complete(Long userId, Long questId) {
        UserQuest userQuest = userQuestRepository.findByUserIdAndQuestId(userId, questId)
            .orElseThrow(() -> new ApiException(ErrorCode.QUEST_NOT_FOUND, "Quest not found"));
        if (userQuest.getStatus() == QuestStatus.COMPLETED) {
            throw new ApiException(ErrorCode.QUEST_ALREADY_COMPLETED, "Quest already completed");
        }
        if (userQuest.getStatus() != QuestStatus.IN_PROGRESS) {
            throw new ApiException(ErrorCode.INVALID_INPUT, "Quest not in progress");
        }

        User user = userQuest.getUser();
        Quest quest = userQuest.getQuest();

        long xpEarned = calculateXp(quest, user.getStreakDays());
        long goldEarned = calculateGold(quest);
        int statPoints = calculateStatPoints(quest.getBaseXp());

        user.addXp(xpEarned);
        user.setGold(user.getGold() + goldEarned);

        UserStats stats = user.getStats();
        if (stats != null && quest.getTargetStat() != null) {
            stats.addStat(quest.getTargetStat(), statPoints);
        }

        boolean leveledUp = applyLevelUp(user);
        updateStreak(user);

        userQuest.setStatus(QuestStatus.COMPLETED);
        userQuest.setCompletedAt(LocalDateTime.now());
        userQuest.setXpEarned((int) xpEarned);
        userQuest.setGoldEarned((int) goldEarned);

        return QuestCompletionResponse.builder()
            .xpEarned(xpEarned)
            .goldEarned(goldEarned)
            .statPoints(statPoints)
            .newLevel(user.getLevel())
            .leveledUp(leveledUp)
            .build();
    }

    @Transactional
    public void abandon(Long userId, Long questId) {
        UserQuest userQuest = userQuestRepository.findByUserIdAndQuestId(userId, questId)
            .orElseThrow(() -> new ApiException(ErrorCode.QUEST_NOT_FOUND, "Quest not found"));
        userQuest.setStatus(QuestStatus.ABANDONED);
    }

    private long calculateXp(Quest quest, int streakDays) {
        double difficultyMultiplier = 1 + (quest.getDifficulty() - 1) * 0.5;
        double streakMultiplier = 1 + Math.min(streakDays * 0.02, 0.6);
        double guildMultiplier = quest.getType() == QuestType.GUILD ? 1.5 : 1.0;
        double xp = quest.getBaseXp() * difficultyMultiplier * streakMultiplier * guildMultiplier;
        return Math.round(xp);
    }

    private long calculateGold(Quest quest) {
        double gold = quest.getBaseXp() * 0.1 * quest.getDifficulty();
        return Math.round(gold);
    }

    private int calculateStatPoints(int baseXp) {
        return (int) Math.ceil(baseXp / 50.0);
    }

    private boolean applyLevelUp(User user) {
        boolean leveledUp = false;
        while (user.getCurrentXp() >= requiredXp(user.getLevel())) {
            long required = requiredXp(user.getLevel());
            user.setCurrentXp(user.getCurrentXp() - required);
            user.setLevel(user.getLevel() + 1);
            user.setStatPoints(user.getStatPoints() + 3);
            leveledUp = true;
        }
        return leveledUp;
    }

    private long requiredXp(int level) {
        return Math.round(100 * Math.pow(level, 1.5));
    }

    private void updateStreak(User user) {
        LocalDate today = LocalDate.now();
        LocalDate last = user.getLastLoginDate();
        if (last == null) {
            user.setStreakDays(1);
        } else if (last.isEqual(today)) {
            return;
        } else if (last.plusDays(1).isEqual(today)) {
            user.setStreakDays(user.getStreakDays() + 1);
        } else {
            user.setStreakDays(1);
        }
        user.setLastLoginDate(today);
    }
}
