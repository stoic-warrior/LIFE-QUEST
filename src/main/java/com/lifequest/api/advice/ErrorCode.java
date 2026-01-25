package com.lifequest.api.advice;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND"),
    QUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "QUEST_NOT_FOUND"),
    GUILD_NOT_FOUND(HttpStatus.NOT_FOUND, "GUILD_NOT_FOUND"),
    MONSTER_NOT_FOUND(HttpStatus.NOT_FOUND, "MONSTER_NOT_FOUND"),
    HUNTING_GROUND_NOT_FOUND(HttpStatus.NOT_FOUND, "HUNTING_GROUND_NOT_FOUND"),
    BOSS_SUMMON_NOT_FOUND(HttpStatus.NOT_FOUND, "BOSS_SUMMON_NOT_FOUND"),
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "ITEM_NOT_FOUND"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "DUPLICATE_EMAIL"),
    QUEST_ALREADY_COMPLETED(HttpStatus.CONFLICT, "QUEST_ALREADY_COMPLETED"),
    ALREADY_IN_BATTLE(HttpStatus.CONFLICT, "ALREADY_IN_BATTLE"),
    NOT_IN_BATTLE(HttpStatus.BAD_REQUEST, "NOT_IN_BATTLE"),
    LEVEL_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "LEVEL_NOT_ENOUGH"),
    HUNTING_GROUND_LOCKED(HttpStatus.BAD_REQUEST, "HUNTING_GROUND_LOCKED"),
    BOSS_NOT_UNLOCKED(HttpStatus.BAD_REQUEST, "BOSS_NOT_UNLOCKED"),
    CANNOT_FLEE_BOSS(HttpStatus.BAD_REQUEST, "CANNOT_FLEE_BOSS"),
    NOT_ENOUGH_GOLD(HttpStatus.BAD_REQUEST, "NOT_ENOUGH_GOLD"),
    STAT_POINTS_INSUFFICIENT(HttpStatus.BAD_REQUEST, "STAT_POINTS_INSUFFICIENT"),
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMIT_EXCEEDED"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");

    private final HttpStatus status;
    private final String code;

    ErrorCode(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}