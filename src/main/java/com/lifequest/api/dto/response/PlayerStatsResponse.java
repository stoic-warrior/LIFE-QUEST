package com.lifequest.api.dto.response;

import com.lifequest.domain.user.PlayerStats;

public record PlayerStatsResponse(
        int atk,
        int def,
        int pen,
        int luk,
        int atkWithEquip,
        int defWithEquip,
        int penWithEquip,
        int lukWithEquip,
        int statPoints
) {
    public static PlayerStatsResponse from(PlayerStats stats, int atkBonus, int defBonus, int penBonus, int lukBonus) {
        return new PlayerStatsResponse(
                stats.getAtk(),
                stats.getDef(),
                stats.getPen(),
                stats.getLuk(),
                stats.getAtk() + atkBonus,
                stats.getDef() + defBonus,
                stats.getPen() + penBonus,
                stats.getLuk() + lukBonus,
                stats.getStatPoints()
        );
    }
}
