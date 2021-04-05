package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthUtil {

    public static String getQrImageURL(String key) {
        return "https://chart.googleapis.com/chart?chs=128x128&cht=qr&chl=200x200&chld=M|0&cht=qr&chl=otpauth://totp/" + CorePlugin.getInstance().getServerManager().getNetwork().getServerName() + "?secret=" + key;
    }

    public static int getHotbarSlotOfItem(ItemStack item, Player player) {
        if (item == null) return -1;
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getItem(i) != null && player.getInventory().getItem(i).equals(item)) {
                return i;
            }
        }
        return 0;
    }

    public static void removeQrMapFromInventory(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);

            if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getLore().get(0).contains("QR")) {
                player.getInventory().remove(item);
            }
        }
    }

    public static boolean checkCode(String key, int code) {
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        return googleAuthenticator.authorize(key, code);
    }

    public static String getDetailedTime(long millis) {
        long seconds = millis / 1000L;

        if (seconds <= 0) {
            return "0 seconds";
        }

        long minutes = seconds / 60;
        seconds = seconds % 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        long day = hours / 24;
        hours = hours % 24;
        long years = day / 365;
        day = day % 365;

        StringBuilder time = new StringBuilder();

        if (years != 0) {
            time.append(years).append(years == 1 ? "y" : "y").append(day == 0 ? "" : ",");
        }

        if (day != 0) {
            time.append(day).append(day == 1 ? "d" : "d").append(hours == 0 ? "" : ",");
        }

        if (hours != 0) {
            time.append(hours).append(hours == 1 ? "h" : "h").append(minutes == 0 ? "" : ",");
        }

        if (minutes != 0) {
            time.append(minutes).append(minutes == 1 ? "m" : "m").append(seconds == 0 ? "" : ",");
        }

        if (seconds != 0) {
            time.append(seconds).append(seconds == 1 ? "s" : "s");
        }

        return time.toString().trim();
    }

    public static long parseTime(String time) {
        long totalTime = 0L;
        boolean found = false;
        Matcher matcher = Pattern.compile("\\d+\\D+").matcher(time);

        while (matcher.find()) {
            String s = matcher.group();
            long value = Long.parseLong(s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
            String type = s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1];

            switch (type) {
                case "s": {
                    totalTime += value;
                    found = true;
                    continue;
                }
                case "m": {
                    totalTime += value * 60L;
                    found = true;
                    continue;
                }
                case "h": {
                    totalTime += value * 60L * 60L;
                    found = true;
                    continue;
                }
                case "d": {
                    totalTime += value * 60L * 60L * 24L;
                    found = true;
                    continue;
                }
                case "w": {
                    totalTime += value * 60L * 60L * 24L * 7L;
                    found = true;
                    continue;
                }
                case "mo": {
                    totalTime += value * 60L * 60L * 24L * 30L;
                    found = true;
                    continue;
                }
                case "y": {
                    totalTime += value * 60L * 60L * 24L * 365L;
                    found = true;
                }
            }
        }

        return found ? (totalTime * 1000L) + 1000L : -1;
    }
}
