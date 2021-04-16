package com.solexgames.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
@AllArgsConstructor
public enum StaffUpdateType {

    HELPOP("&2[H] ", "HELPOP", "scandium.staff"),
    REPORT("&c[R] ", "REPORT", "scandium.staff"),
    FREEZE("&4[F] ", "FREEZE", "scandium.staff"),
    UNFREEZE("&4[F] ", "UNFREEZE", "scandium.staff");

    private final String prefix;
    private final String name;
    private final String permission;

}
