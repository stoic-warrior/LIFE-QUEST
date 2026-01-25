package com.lifequest.application.service;

import com.lifequest.api.advice.ApiException;
import com.lifequest.api.advice.ErrorCode;
import com.lifequest.api.dto.response.HuntingGroundResponse;
import com.lifequest.domain.battle.BattleProgress;
import com.lifequest.domain.battle.BattleProgressRepository;
import com.lifequest.domain.boss.BossClearRepository;
import com.lifequest.domain.hunting.HuntingGround;
import com.lifequest.domain.hunting.HuntingGroundRepository;
import com.lifequest.domain.hunting.MonsterKillCount;
import com.lifequest.domain.hunting.MonsterKillCountRepository;
import com.lifequest.domain.user.User;
import com.lifequest.domain.user.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HuntingService {

    private final HuntingGroundRepository huntingGroundRepository;
    private final BattleProgressRepository battleProgressRepository;
    private final BossClearRepository bossClearRepository;
    private final MonsterKillCountRepository killCountRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<HuntingGroundResponse> getHuntingGrounds(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<HuntingGround> grounds = huntingGroundRepository.findAllByOrderByRequiredLevelAsc();
        Set<Long> clearedBossIds = Set.copyOf(bossClearRepository.findClearedBossIdsByUserId(userId));
        
        // 유저의 몬스터별 처치 수
        Map<Long, Integer> killCounts = killCountRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(
                        mkc -> mkc.getMonster().getId(),
                        MonsterKillCount::getKillCount
                ));

        return grounds.stream()
                .map(hg -> {
                    boolean unlocked = isHuntingGroundUnlocked(user, hg, clearedBossIds);
                    int killCount = killCounts.getOrDefault(hg.getMonster().getId(), 0);
                    return HuntingGroundResponse.from(hg, unlocked, killCount);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void enterHuntingGround(Long userId, Long huntingGroundId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        HuntingGround huntingGround = huntingGroundRepository.findById(huntingGroundId)
                .orElseThrow(() -> new ApiException(ErrorCode.HUNTING_GROUND_NOT_FOUND));

        Set<Long> clearedBossIds = Set.copyOf(bossClearRepository.findClearedBossIdsByUserId(userId));

        if (!isHuntingGroundUnlocked(user, huntingGround, clearedBossIds)) {
            throw new ApiException(ErrorCode.HUNTING_GROUND_LOCKED);
        }

        BattleProgress progress = battleProgressRepository.findByUserId(userId)
                .orElseGet(() -> {
                    BattleProgress newProgress = BattleProgress.create(user);
                    return battleProgressRepository.save(newProgress);
                });

        if (progress.isInBattle()) {
            throw new ApiException(ErrorCode.ALREADY_IN_BATTLE);
        }

        progress.startHunting(huntingGround);
    }

    @Transactional
    public void leaveHuntingGround(Long userId) {
        BattleProgress progress = battleProgressRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_IN_BATTLE));

        if (!progress.isHunting()) {
            throw new ApiException(ErrorCode.NOT_IN_BATTLE);
        }

        progress.reset();
    }

    /**
     * 사냥터 해금 조건:
     * 1. 유저 레벨 >= 요구 레벨
     * 2. 해금 보스 클리어 (있는 경우)
     */
    private boolean isHuntingGroundUnlocked(User user, HuntingGround hg, Set<Long> clearedBossIds) {
        // 레벨 체크
        if (user.getLevel() < hg.getRequiredLevel()) {
            return false;
        }

        // 해금 보스 체크 (없으면 기본 해금)
        if (hg.getUnlockBoss() == null) {
            return true;
        }

        return clearedBossIds.contains(hg.getUnlockBoss().getId());
    }
}
