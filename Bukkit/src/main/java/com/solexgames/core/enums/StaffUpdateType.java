package com.solexgames.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StaffUpdateType {

    HELPOP("&2[H] ", "HELPOP", "scandium.staff"),
    REPORT("&c[R] ", "REPORT", "scandium.staff"),
    FREEZE("&3[S] ", "FREEZE", "scandium.staff"),
    UNFREEZE("&3[S] ", "UNFREEZE", "scandium.staff");

    private final String prefix;
    private final String name;
    private final String permission;

}
