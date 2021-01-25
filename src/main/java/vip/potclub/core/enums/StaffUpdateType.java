package vip.potclub.core.enums;

import lombok.Getter;

@Getter
public enum StaffUpdateType {

    HELPOP("&2[H] ", "HELPOP", "scandium.staff"),
    REPORT("&c[R] ", "REPORT", "scandium.staff"),
    FREEZE("&4[F] ", "FREEZE", "scandium.staff"),
    UNFREEZE("&4[F] ", "UNFREEZE", "scandium.staff");

    private final String prefix;
    public final String name;
    public final String permission;

    StaffUpdateType(String prefix, String name, String permission) {
        this.prefix = prefix;
        this.permission = permission;
        this.name = name;
    }
}
