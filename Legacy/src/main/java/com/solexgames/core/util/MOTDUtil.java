package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;

/**
 * @author GrowlyX
 * @since 3/4/2021
 */

public final class MOTDUtil {

    public static String getNormalMotd() {
        return Color.translate(
                CorePlugin.getInstance().getMotdConfig().getString("motd.normal-motd")
                .replace("<region>", CorePlugin.getInstance().getConfig().getString("region"))
                .replace("<bar>", Character.toString('┃'))
                .replace("<nl>", "\n")
        );
    }

    public static String getWhitelistedMotd() {
        return Color.translate(
                CorePlugin.getInstance().getMotdConfig().getString("motd.whitelisted-motd")
                .replace("<region>", CorePlugin.getInstance().getConfig().getString("region"))
                .replace("<bar>", Character.toString('┃'))
                .replace("<nl>", "\n")
        );
    }
}
