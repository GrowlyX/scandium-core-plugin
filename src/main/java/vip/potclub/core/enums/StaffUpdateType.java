package vip.potclub.core.enums;

import lombok.Getter;

@Getter
public enum StaffUpdateType {

    HELPOP("&2[H] ", "HELPOP", "core.staff"),
    REPORT("&c[R] ", "REPORT", "core.staff"),
    FREEZE("&4[F] ", "FREEZE", "core.staff"),
    UNFREEZE("&4[F] ", "UNFREEZE", "core.staff");

    public final String prefix;
    public final String name;
    public final String permission;

    StaffUpdateType(String prefix, String name, String permission) {
        this.prefix = prefix;
        this.permission = permission;
        this.name = name;
    }
}
