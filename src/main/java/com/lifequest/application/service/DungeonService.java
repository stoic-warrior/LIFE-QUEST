package com.lifequest.application.service;

import com.lifequest.api.advice.ApiException;
import com.lifequest.api.advice.ErrorCode;
import com.lifequest.api.dto.response.DungeonResponse;
import com.lifequest.domain.dungeon.Dungeon;
import com.lifequest.domain.dungeon.DungeonProgress;
import com.lifequest.domain.dungeon.DungeonProgressRepository;
import com.lifequest.domain.dungeon.DungeonRepository;
import com.lifequest.domain.user.User;
import com.lifequest.domain.user.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DungeonService {

    private final DungeonRepository dungeonRepository;
    private final DungeonProgressRepository progressRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<DungeonResponse> getDungeonList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<Dungeon> dungeons = dungeonRepository.findAllByOrderByFloorNumberAsc();

        return dungeons.stream()
                .map(d -> DungeonResponse.from(d, user.getLevel() >= d.getRequiredLevel()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void enterDungeon(Long userId, Long dungeonId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Dungeon dungeon = dungeonRepository.findById(dungeonId)
                .orElseThrow(() -> new ApiException(ErrorCode.DUNGEON_NOT_FOUND));

        if (user.getLevel() < dungeon.getRequiredLevel()) {
            throw new ApiException(ErrorCode.LEVEL_NOT_ENOUGH);
        }

        DungeonProgress progress = progressRepository.findByUserId(userId)
                .orElseGet(() -> {
                    DungeonProgress newProgress = DungeonProgress.create(user);
                    return progressRepository.save(newProgress);
                });

        if (progress.isInBattle()) {
            throw new ApiException(ErrorCode.ALREADY_IN_BATTLE);
        }

        progress.startDungeon(dungeon);
    }

    @Transactional
    public void leaveDungeon(Long userId) {
        DungeonProgress progress = progressRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_IN_BATTLE));

        if (!progress.isInBattle()) {
            throw new ApiException(ErrorCode.NOT_IN_BATTLE);
        }

        progress.reset();
    }
}
