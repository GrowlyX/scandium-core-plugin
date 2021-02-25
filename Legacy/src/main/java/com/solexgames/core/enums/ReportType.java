package com.solexgames.core.enums;

import lombok.Getter;

@Getter
public enum ReportType {

    COMBAT_HACKS("Combat Hacks"),
    CHAT_ABUSE("Chat Abuse"),
    GAME_SABOTAGE("Game Sabotage"),
    OTHER("Other");

    private final String name;

    ReportType(String name) {
        this.name = name;
    }
}
