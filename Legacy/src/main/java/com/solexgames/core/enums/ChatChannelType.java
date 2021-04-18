package com.solexgames.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.beans.ConstructorProperties;

@Getter
@RequiredArgsConstructor
public enum ChatChannelType {

    STAFF("&3[S] ","scandium.channels.staff"),
    ADMIN("&c[A] ","scandium.channels.admin"),
    HOST("&2[H] ","scandium.channels.host"),
    MANAGER("&4[M] ","scandium.channels.manager"),
    DEV("&c[D] ","scandium.channels.dev"),
    OWNER("&9[O] ","scandium.channels.owner");

    private final String prefix;
    private final String permission;

}
