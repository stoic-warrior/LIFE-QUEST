package com.lifequest.api.dto.response;

import com.lifequest.domain.user.PlayerStats;

public record PlayerStatsResponse(
        int atk,
        int def,
        int crt,
        int luk,
        int atkWithEquip,
        int defWithEquip,
        int crtWithEquip,
        int lukWithEquip
) {
    public static PlayerStatsResponse from(PlayerStats stats, int atkBonus, int defBonus, int crtBonus, int lukBonus) {
        return new PlayerStatsResponse(
                stats.getAtk(),
                stats.getDef(),
                stats.getCrt(),
                stats.getLuk(),
                stats.getAtk() + atkBonus,
                stats.getDef() + defBonus,
                stats.getCrt() + crtBonus,
                stats.getLuk() + lukBonus
        );
    }
}
