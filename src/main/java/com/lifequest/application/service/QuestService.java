package com.lifequest.application.service;

import com.lifequest.api.advice.ApiException;
import com.lifequest.api.advice.ErrorCode;
import com.lifequest.api.dto.request.QuestCreateRequest;
import com.lifequest.api.dto.response.QuestCompleteResponse;
import com.lifequest.api.dto.response.QuestResponse;
import com.lifequest.domain.quest.Quest;
import com.lifequest.domain.quest.QuestRepository;
import com.lifequest.domain.quest.QuestStatus;
import com.lifequest.domain.user.User;
import com.lifequest.domain.user.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;
    private final UserRepository userRepository;
    private final BattleService battleService;

    @Transactional(readOnly = true)
    public List<QuestResponse> getQuests(Long userId, String status) {
        List<Quest> quests;
        if (status != null && !status.isEmpty()) {
            quests = questRepository.findByUserIdAndStatus(userId, QuestStatus.valueOf(status));
        } else {
            quests = questRepository.findByUserId(userId);
        }
        return quests.stream().map(QuestResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public QuestResponse createQuest(Long userId, QuestCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Quest quest = Quest.create(
                user,
                request.title(),
                request.description(),
                request.difficulty(),
                request.deadline()
        );

        return QuestResponse.from(questRepository.save(quest));
    }

    @Transactional
    public QuestCompleteResponse completeQuest(Long userId, Long questId) {
        Quest quest = questRepository.findByIdAndUserId(questId, userId)
                .orElseThrow(() -> new ApiException(ErrorCode.QUEST_NOT_FOUND));

        if (quest.getStatus() == QuestStatus.COMPLETED) {
            throw new ApiException(ErrorCode.QUEST_ALREADY_COMPLETED);
        }

        // 퀘스트 완료 처리
        quest.complete();

        // 스트릭 업데이트
        User user = userRepository.findById(userId).orElseThrow();
        updateStreak(user);

        // 전투 중이면 몬스터 공격
        return battleService.attackMonster(userId, quest);
    }

    @Transactional
    public void deleteQuest(Long userId, Long questId) {
        Quest quest = questRepository.findByIdAndUserId(questId, userId)
                .orElseThrow(() -> new ApiException(ErrorCode.QUEST_NOT_FOUND));
        questRepository.delete(quest);
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
