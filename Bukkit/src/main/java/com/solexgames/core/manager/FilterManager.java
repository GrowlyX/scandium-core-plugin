package com.solexgames.core.manager;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class FilterManager {

    private final Pattern urlRegex = Pattern.compile("^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$");
    private final Pattern ipRegex = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
    private final Pattern otherIpRegex = Pattern.compile("(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])" + "\\." + "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])" + "\\." + "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])" + "\\." + "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])");

    private final CorePlugin plugin = CorePlugin.getInstance();
    private final List<String> filteredMessages = CorePlugin.getInstance().getFilterConfig().getStringList("messages");

    public boolean isDmFiltered(Player player, String target, String message) {
        final boolean filtered = this.isStringFiltered(message);

        this.handleSocialSpy(player, target, message);

        if (filtered) {
            this.handleDmAlert(player, target, message);
        }

        return filtered;
    }

    public boolean isMessageFiltered(Player player, String message) {
        final boolean filtered = this.isStringFiltered(message);

        if (filtered) {
            this.handleAlert(player, message);
        }

        return filtered;
    }

    private boolean isStringFiltered(String message) {
        final String fixedMessage = message.toLowerCase()
                .replace("3", "e")
                .replace("1", "i")
                .replace("!", "i")
                .replace("@", "a")
                .replace("7", "t")
                .replace("0", "o")
                .replace("5", "s")
                .replace("8", "b")
                .replaceAll("\\p{Punct}|\\d", "")
                .trim();
        final String[] words = fixedMessage.replace("(dot)", ".").replace("[dot]", ".").replace("<dot>", ".").trim().split(" ");

        for (String filtered : this.filteredMessages) {
            if (!fixedMessage.contains(filtered.toLowerCase())) {
                return true;
            }
        }

        for (String word : words) {
            final Matcher ipMatcher = this.ipRegex.matcher(word);
            final Matcher otherIpMatcher = this.otherIpRegex.matcher(word);
            final Matcher urlMatcher = this.urlRegex.matcher(word);

            if (ipMatcher.matches() || otherIpMatcher.matches() || urlMatcher.matches()) {
                return true;
            }
        }

        return false;
    }

    private void handleAlert(Player player, String message) {
        final PotPlayer targetPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        PlayerUtil.sendToFiltered("&c[Filtered] &e" + targetPlayer.getColorByRankColor() + targetPlayer.getName() + "&7: &e" + message);
    }

    private void handleSocialSpy(Player player, String target, String message) {
        final PotPlayer targetPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        PlayerUtil.sendToSocialSpy("&c[Social Spy] &7(" + targetPlayer.getName() + " -> " + target + ")" + "&7: &e" + message);
    }

    private void handleDmAlert(Player player, String target, String message) {
        final PotPlayer targetPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        PlayerUtil.sendToFiltered("&c[Filtered] &7(" + targetPlayer.getName() + " -> " + target + ")" + "&7: &e" + message);
    }
}
