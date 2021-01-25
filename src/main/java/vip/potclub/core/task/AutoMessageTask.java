package vip.potclub.core.task;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AutoMessageTask extends BukkitRunnable { // Credits: https://github.com/ConaxGames/cSuite/tree/main/plugin/src/main/java/com/conaxgames/task/AutoMessageTask.java

    private final List<String> defaultMessages = new ArrayList<>();

    private int lastCount;

    public AutoMessageTask() {
        setupMessages();
        runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 120 * 20L);
    }

    @Override
    public void run() {
        sendMessage(defaultMessages);
    }

    private void setupMessages() {
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        String prefix = serverType.getMainColor() + "[TIP] " + serverType.getSecondaryColor();

        defaultMessages.add(prefix + "Follow our Twitter account for news and giveaways - www.twitter.com/" + serverType.getTwitterLink());
        defaultMessages.add(prefix + "Join our Discord server to chat with players, get support, and see sneak peeks - " + serverType.getDiscordLink());
        defaultMessages.add(prefix + "Purchase ranks, perks, and more on our shop - " + serverType.getStoreLink());
        defaultMessages.add(prefix + "Configure our systems to your liking by using /settings.");
        defaultMessages.add(prefix + "Change your network language type using /language.");
        defaultMessages.add(prefix + "Punished? Appeal on our Discord server or purchase to remove your punishment.");
        defaultMessages.add(prefix + "Donators can host events using /event.");
        defaultMessages.add(prefix + "Butterfly clicking may result in a punishment! Use at your own risk.");
        defaultMessages.add(prefix + "View the leaderboards on our website - " + serverType.getWebsiteLink());
    }

    private void sendMessage(List<String> input) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            PotPlayer potPlayer = PotPlayer.getPlayer(player);
            if (potPlayer.isCanSeeTips()) {
                player.sendMessage("  ");
                int count = CorePlugin.RANDOM.nextInt(defaultMessages.size());
                player.sendMessage(Color.translate(input.get(this.lastCount == count ? CorePlugin.RANDOM.nextInt(input.size()) : count)));
                this.lastCount = count;
                player.sendMessage("  ");
            }
        });
    }
}
