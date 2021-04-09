package com.solexgames.core.enums;

import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
public enum ChatChannelType {

    STAFF("&3[S] ","scandium.channels.staff"),
    ADMIN("&c[A] ","scandium.channels.admin"),
    HOST("&2[H] ","scandium.channels.host"),
    MANAGER("&4[M] ","scandium.channels.manager"),
    DEV("&c[D] ","scandium.channels.dev"),
    OWNER("&9[O] ","scandium.channels.owner");

    private final String prefix;
    private final String permission;

    @ConstructorProperties({"prefix", "permission"})
    ChatChannelType(String prefix, String permission) {
        this.prefix = prefix;
        this.permission = permission;
    }
}
