package com.lifequest.application.service;

import com.lifequest.api.advice.ApiException;
import com.lifequest.api.advice.ErrorCode;
import com.lifequest.api.dto.request.AllocateStatRequest;
import com.lifequest.api.dto.response.PlayerStatsResponse;
import com.lifequest.domain.item.UserItem;
import com.lifequest.domain.item.UserItemRepository;
import com.lifequest.domain.user.PlayerStats;
import com.lifequest.domain.user.PlayerStatsRepository;
import com.lifequest.domain.user.User;
import com.lifequest.domain.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PlayerStatsRepository playerStatsRepository;
    private final UserItemRepository userItemRepository;

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public PlayerStatsResponse getStats(Long userId) {
        User user = getUser(userId);
        PlayerStats stats = user.getStats();
        if (stats == null) {
            stats = PlayerStats.create();
        }

        int[] bonuses = getEquipmentBonuses(userId);
        return PlayerStatsResponse.from(stats, bonuses[0], bonuses[1], bonuses[2], bonuses[3]);
    }

    @Transactional
    public PlayerStatsResponse allocateStats(Long userId, AllocateStatRequest request) {
        User user = getUser(userId);

        if (user.getStatPoints() < request.points()) {
            throw new ApiException(ErrorCode.STAT_POINTS_INSUFFICIENT);
        }

        PlayerStats stats = user.getStats();
        if (stats == null) {
            stats = PlayerStats.create();
            user.initializeStats(stats);
        }

        stats.addStat(request.stat(), request.points());
        user.setStatPoints(user.getStatPoints() - request.points());

        int[] bonuses = getEquipmentBonuses(userId);
        return PlayerStatsResponse.from(stats, bonuses[0], bonuses[1], bonuses[2], bonuses[3]);
    }

    private int[] getEquipmentBonuses(Long userId) {
        List<UserItem> equipped = userItemRepository.findByUserIdAndEquippedTrue(userId);
        int atkBonus = 0, defBonus = 0, crtBonus = 0, lukBonus = 0;

        for (UserItem ui : equipped) {
            atkBonus += ui.getItem().getAtkBonus();
            defBonus += ui.getItem().getDefBonus();
            crtBonus += ui.getItem().getCrtBonus();
            lukBonus += ui.getItem().getLukBonus();
        }

        return new int[]{atkBonus, defBonus, crtBonus, lukBonus};
    }
}
