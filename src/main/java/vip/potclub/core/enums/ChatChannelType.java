package vip.potclub.core.enums;

import lombok.Getter;

@Getter
public enum ChatChannelType {

    STAFF("&3[S] ", "STAFF", "scandium.channels.staff"),
    ADMIN("&c[A] ", "ADMIN", "scandium.channels.admin"),
    HOST("&2[H] ", "HOST", "scandium.channels.host"),
    DEV("&c[D] ", "DEV", "scandium.channels.dev");

    private final String prefix;
    private final String name;
    private final String permission;

    ChatChannelType(String prefix, String name, String permission) {
        this.prefix = prefix;
        this.permission = permission;
        this.name = name;
    }
}
