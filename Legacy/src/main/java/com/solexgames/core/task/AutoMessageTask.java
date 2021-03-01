package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class AutoMessageTask extends BukkitRunnable {

    private final List<String> allTips = new ArrayList<>();

    private final String tipPrefix;
    private final boolean padding;
    private final boolean prefix;

    private int lastCount;

    public AutoMessageTask() {
        this.tipPrefix = Color.translate(CorePlugin.getInstance().getConfig().getString("tips.prefix"));
        this.padding = CorePlugin.getInstance().getConfig().getBoolean("tips.padding");
        this.prefix = CorePlugin.getInstance().getConfig().getBoolean("tips.use-prefix");

        this.allTips.addAll(CorePlugin.getInstance().getConfig().getStringList("tips.messages"));

        runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, CorePlugin.getInstance().getConfig().getInt("tips.interval") * 20L);
    }

    @Override
    public void run() {
        sendToOnline(allTips);
    }

    private void sendToOnline(List<String> input) {
        Bukkit.getOnlinePlayers()
                .stream()
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(PotPlayer::isCanSeeTips)
                .forEach(potPlayer -> {
                    if (padding) {
                        potPlayer.getPlayer().sendMessage("  ");
                        executeMessage(input, potPlayer);
                        potPlayer.getPlayer().sendMessage("  ");
                    } else {
                        executeMessage(input, potPlayer);
                    }
                });
    }

    private void executeMessage(List<String> input, PotPlayer potPlayer) {
        int count = CorePlugin.RANDOM.nextInt(allTips.size());
        potPlayer.getPlayer().sendMessage(Color.translate((this.prefix ? this.tipPrefix : "") + input.get(this.lastCount == count ? CorePlugin.RANDOM.nextInt(input.size()) : count).replace("<nl>", "\n")));
        this.lastCount = count;
    }
}
