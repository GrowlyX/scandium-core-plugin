package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GrowlyX
 * @since March 2021
 */

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

        this.runTaskTimerAsynchronously(
                CorePlugin.getInstance(),
                20L,
                CorePlugin.getInstance().getConfig().getInt("tips.interval") * 20L
        );
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().stream()
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(PotPlayer::isCanSeeTips)
                .forEach(potPlayer -> {
                    if (padding) {
                        potPlayer.getPlayer().sendMessage("  ");
                        this.executeMessage(potPlayer);
                        potPlayer.getPlayer().sendMessage("  ");

                    } else {
                        this.executeMessage(potPlayer);
                    }
                });
    }

    private void executeMessage(PotPlayer potPlayer) {
        String prefix = (this.prefix ? this.tipPrefix : "");
        String tipString = this.allTips.get(this.lastCount).replace("<nl>", "\n");
        String newMessage = Color.translate(prefix + tipString);

        potPlayer.getPlayer().sendMessage(newMessage);

        if (this.lastCount == this.allTips.size()) {
            this.lastCount = 0;
        } else {
            this.lastCount++;
        }
    }
}
