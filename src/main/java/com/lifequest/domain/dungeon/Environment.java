package com.lifequest.domain.dungeon;

public enum Environment {
    PLAIN("초원"),
    SWAMP("늪지"),
    VOLCANO("화산"),
    GLACIER("빙하");

    private final String displayName;

    Environment(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
