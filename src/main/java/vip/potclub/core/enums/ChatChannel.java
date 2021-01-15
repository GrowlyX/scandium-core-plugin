package vip.potclub.core.enums;

import lombok.Getter;

@Getter
public enum ChatChannel {

    STAFF("&3[S] ", "STAFF", "core.staff"),
    ADMIN("&c[A] ", "ADMIN", "core.admin"),
    HOST("&2[H] ", "HOST", "core.host"),
    DEV("&c[D] ", "DEV", "core.dev");

    public final String prefix;
    public final String name;
    public final String permission;

    ChatChannel(String prefix, String name, String permission) {
        this.prefix = prefix;
        this.permission = permission;
        this.name = name;
    }
}
