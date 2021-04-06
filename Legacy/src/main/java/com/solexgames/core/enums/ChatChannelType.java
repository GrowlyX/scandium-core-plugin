package com.solexgames.core.enums;

import lombok.Getter;

@Getter
public enum ChatChannelType {

    STAFF("&3[S] ", "STAFF", "scandium.channels.staff"),
    ADMIN("&c[A] ", "ADMIN", "scandium.channels.admin"),
    HOST("&2[H] ", "HOST", "scandium.channels.host"),
    MANAGER("&4[M] ", "MANAGER", "scandium.channels.manager"),
    DEV("&c[D] ", "DEV", "scandium.channels.dev"),
    OWNER("&9[O] ", "OWNER", "scandium.channels.owner");

    private final String prefix;
    private final String name;
    private final String permission;

    ChatChannelType(String prefix, String name, String permission) {
        this.prefix = prefix;
        this.permission = permission;
        this.name = name;
    }
}
