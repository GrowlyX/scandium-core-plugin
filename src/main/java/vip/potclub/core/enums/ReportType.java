package vip.potclub.core.enums;

import lombok.Getter;

@Getter
public enum ReportType {

    COMBAT_HACKS("Combat Hacks"),
    CHAT_ABUSE("Chat Abuse"),
    GAME_SABOTAGE("Game Sabotage"),
    OTHER("Other");

    public final String name;

    ReportType(String name) {
        this.name = name;
    }
}
