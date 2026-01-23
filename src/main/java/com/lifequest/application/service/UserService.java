package com.lifequest.application.service;

import com.lifequest.api.advice.ApiException;
import com.lifequest.api.advice.ErrorCode;
import com.lifequest.api.dto.request.AllocateStatRequest;
import com.lifequest.api.dto.request.UserUpdateRequest;
import com.lifequest.domain.user.User;
import com.lifequest.domain.user.UserRepository;
import com.lifequest.domain.user.UserStats;
import com.lifequest.domain.user.UserStatsRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserStatsRepository userStatsRepository;

    public UserService(UserRepository userRepository, UserStatsRepository userStatsRepository) {
        this.userRepository = userRepository;
        this.userStatsRepository = userStatsRepository;
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "User not found"));
    }

    @Transactional
    public User updateProfile(Long userId, UserUpdateRequest request) {
        User user = getUser(userId);
        if (request.getNickname() != null && !request.getNickname().isBlank()) {
            user.setNickname(request.getNickname());
        }
        return user;
    }

    @Transactional
    public UserStats allocateStats(Long userId, AllocateStatRequest request) {
        User user = getUser(userId);
        if (user.getStatPoints() < request.getPoints()) {
            throw new ApiException(ErrorCode.STAT_POINTS_INSUFFICIENT, "Not enough stat points");
        }
        UserStats stats = user.getStats();
        if (stats == null) {
            stats = UserStats.create();
            user.initializeStats(stats);
        }
        stats.addStat(request.getStat(), request.getPoints());
        user.setStatPoints(user.getStatPoints() - request.getPoints());
        userStatsRepository.save(stats);
        return stats;
    }

    public List<User> getRanking(int limit, int offset) {
        int page = offset / limit;
        return userRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "totalXp")))
            .getContent();
    }
}
