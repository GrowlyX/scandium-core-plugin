package com.solexgames.core.enums;

import org.bukkit.ChatColor;

public enum StaffUpdateType {

    REQUEST,
    REPORT,
    FREEZE,
    UNFREEZE;

    public String getPermission() {
        return "scandium.staff";
    }

    public String getPrefix() {
        return ChatColor.AQUA + "[S] ";
    }
}
