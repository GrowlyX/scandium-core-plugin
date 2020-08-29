package me.growlyx.core.utils;

import org.bukkit.ChatColor;

public class Style {

    public static String colorPing(int ping) {
        if (ping <= 40) {
            return CC.translate("&a" + ping);
        } else if (ping <= 70) {
            return CC.translate("&e" + ping);
        } else if (ping <= 100) {
            return CC.translate("&6" + ping);
        } else {
            return CC.translate("&4" + ping);
        }
    }

}
