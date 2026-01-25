package com.lifequest.application.service;

import com.lifequest.api.advice.ApiException;
import com.lifequest.api.advice.ErrorCode;
import com.lifequest.api.dto.response.BossSummonResponse;
import com.lifequest.domain.battle.BattleProgress;
import com.lifequest.domain.battle.BattleProgressRepository;
import com.lifequest.domain.boss.BossClearRepository;
import com.lifequest.domain.boss.BossSummon;
import com.lifequest.domain.boss.BossSummonRepository;
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
public class BossService {

    private final BossSummonRepository bossSummonRepository;
    private final BattleProgressRepository battleProgressRepository;
    private final BossClearRepository bossClearRepository;
    private final MonsterKillCountRepository killCountRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<BossSummonResponse> getBossSummons(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<BossSummon> summons = bossSummonRepository.findAllByOrderByPriceAsc();
        Set<Long> clearedBossIds = Set.copyOf(bossClearRepository.findClearedBossIdsByUserId(userId));
        
        Map<Long, Integer> killCounts = killCountRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(
                        mkc -> mkc.getMonster().getId(),
                        MonsterKillCount::getKillCount
                ));

        return summons.stream()
                .map(bs -> {
                    int currentKills = killCounts.getOrDefault(bs.getRequiredMonster().getId(), 0);
                    boolean unlocked = currentKills >= bs.getRequiredKillCount();
                    boolean cleared = clearedBossIds.contains(bs.getBoss().getId());
                    return BossSummonResponse.from(bs, unlocked, cleared, currentKills, user.getGold());
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void summonBoss(Long userId, Long bossSummonId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        BossSummon summon = bossSummonRepository.findById(bossSummonId)
                .orElseThrow(() -> new ApiException(ErrorCode.BOSS_SUMMON_NOT_FOUND));

        // 해금 조건 체크
        int currentKills = killCountRepository.findByUserIdAndMonsterId(userId, summon.getRequiredMonster().getId())
                .map(MonsterKillCount::getKillCount)
                .orElse(0);

        if (currentKills < summon.getRequiredKillCount()) {
            throw new ApiException(ErrorCode.BOSS_NOT_UNLOCKED);
        }

        // 골드 체크
        if (user.getGold() < summon.getPrice()) {
            throw new ApiException(ErrorCode.NOT_ENOUGH_GOLD);
        }

        // 전투 중인지 체크
        BattleProgress progress = battleProgressRepository.findByUserId(userId)
                .orElseGet(() -> {
                    BattleProgress newProgress = BattleProgress.create(user);
                    return battleProgressRepository.save(newProgress);
                });

        if (progress.isInBattle()) {
            throw new ApiException(ErrorCode.ALREADY_IN_BATTLE);
        }

        // 골드 차감
        user.subtractGold(summon.getPrice());

        // 보스전 시작
        progress.startBossBattle(summon.getBoss());
    }
}
