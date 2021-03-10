package com.solexgames.xenon.util;

import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;

public final class StringUtil {

    public static String buildMessage(String[] args, int start) {
        return start >= args.length ? "" : ChatColor.stripColor(String.join(" ", Arrays.copyOfRange(args, start, args.length)));
    }
}
