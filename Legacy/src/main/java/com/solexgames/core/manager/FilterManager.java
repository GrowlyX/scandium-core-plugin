package com.solexgames.core.manager;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class FilterManager {

    private static final Pattern URL_REGEX = Pattern.compile("^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$");
    private static final Pattern IP_REGEX = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
    private static final Pattern OTHER_IP_REGEX = Pattern.compile("(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])" + "\\." + "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])" + "\\." + "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])" + "\\." + "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])");

    private final CorePlugin plugin;
    private final List<String> filteredMessages;

    public FilterManager() {
        this.plugin = CorePlugin.getInstance();
        this.filteredMessages = this.plugin.getFilterConfig().getStringList("messages");
    }

    public boolean isDmFiltered(Player player, String target, String message) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        String fixedMessage = message.toLowerCase()
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
        String[] words = fixedMessage.replace("(dot)", ".").replace("[dot]", ".").replace("<dot>", ".").trim().split(" ");

        this.filteredMessages.stream()
                .filter(s -> fixedMessage.contains(s.toLowerCase()))
                .forEach(s -> {
                    if (!atomicBoolean.get()) {
                        if (!player.hasPermission("scandium.filter.bypass"))
                            handleDmAlert(player, target, fixedMessage);

                        atomicBoolean.set(true);
                    }
                });

        if (!atomicBoolean.get()) {
            Arrays.asList(words).forEach(word -> {
                Matcher ipMatcher = FilterManager.IP_REGEX.matcher(word);
                if (ipMatcher.matches()) {
                    atomicBoolean.set(true);
                }

                Matcher otherIpMatcher = FilterManager.OTHER_IP_REGEX.matcher(word);
                if (otherIpMatcher.matches()) {
                    atomicBoolean.set(true);
                }

                Matcher urlMatcher = FilterManager.URL_REGEX.matcher(word);
                if (urlMatcher.matches()) {
                    atomicBoolean.set(true);
                }
            });
        }

        if (!atomicBoolean.get()) {
            this.handleSocialSpy(player, target, message);
        }

        return atomicBoolean.get();
    }

    public boolean isMessageFiltered(Player player, String message) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        String fixedMessage = message.toLowerCase()
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
        String[] words = fixedMessage.replace("(dot)", ".").replace("[dot]", ".").replace("<dot>", ".").trim().split(" ");

        this.filteredMessages.stream()
                .filter(s -> fixedMessage.contains(s.toLowerCase()))
                .forEach(s -> {
                    if (!atomicBoolean.get()) {
                        if (!player.hasPermission("scandium.filter.bypass"))
                            handleAlert(player, fixedMessage);

                        atomicBoolean.set(true);
                    }
                });

        if (!atomicBoolean.get()) {
            Arrays.asList(words).forEach(word -> {
                Matcher ipMatcher = FilterManager.IP_REGEX.matcher(word);
                if (ipMatcher.matches()) {
                    atomicBoolean.set(true);
                }

                Matcher otherIpMatcher = FilterManager.OTHER_IP_REGEX.matcher(word);
                if (otherIpMatcher.matches()) {
                    atomicBoolean.set(true);
                }

                Matcher urlMatcher = FilterManager.URL_REGEX.matcher(word);
                if (urlMatcher.matches()) {
                    atomicBoolean.set(true);
                }
            });
        }

        return atomicBoolean.get();
    }

    private void handleAlert(Player player, String message) {
        PotPlayer targetPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        Bukkit.getOnlinePlayers()
                .stream()
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(potPlayer -> potPlayer.isCanSeeFiltered() && potPlayer.getPlayer().hasPermission("scandium.staff"))
                .forEach(potPlayer -> potPlayer.getPlayer().sendMessage(Color.translate("&c[Filtered] &e" + targetPlayer.getColorByRankColor() + targetPlayer.getName() + "&7: &e") + message));
    }

    private void handleSocialSpy(Player player, String target, String message) {
        PotPlayer targetPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        Bukkit.getOnlinePlayers()
                .stream()
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(potPlayer -> potPlayer.isSocialSpy() && potPlayer.getPlayer().hasPermission("scandium.socialspy"))
                .forEach(potPlayer -> potPlayer.getPlayer().sendMessage(Color.translate("&c[Social Spy] &7(" + targetPlayer.getName() + " -> " + target + ")" + "&7: &e") + message));
    }

    private void handleDmAlert(Player player, String target, String message) {
        PotPlayer targetPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        Bukkit.getOnlinePlayers()
                .stream()
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(potPlayer -> potPlayer.isCanSeeFiltered() && potPlayer.getPlayer().hasPermission("scandium.staff"))
                .forEach(potPlayer -> potPlayer.getPlayer().sendMessage(Color.translate("&c[Filtered] &7(" + targetPlayer.getName() + " -> " + target + ")" + "&7: &e") + message));
    }
}
