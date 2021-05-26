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
        this.tipPrefix = Color.translate(CorePlugin.getInstance().getConfig().getString("tip-broadcasts.prefix"));
        this.padding = CorePlugin.getInstance().getConfig().getBoolean("tip-broadcasts.padding");
        this.prefix = CorePlugin.getInstance().getConfig().getBoolean("tip-broadcasts.use-prefix");

        this.allTips.addAll(CorePlugin.getInstance().getConfig().getStringList("tip-broadcasts.messages"));

        this.runTaskTimerAsynchronously(
                CorePlugin.getInstance(),
                20L,
                CorePlugin.getInstance().getConfig().getInt("tip-broadcasts.interval") * 20L
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
        final int count = CorePlugin.RANDOM.nextInt(this.allTips.size());

        potPlayer.getPlayer().sendMessage(Color.translate(
                (this.prefix ? this.tipPrefix : "") + this.allTips.get(this.lastCount == count ? CorePlugin.RANDOM.nextInt(this.allTips.size()) : count).replace("<nl>", "\n")
        ));

        this.lastCount = count;
    }
}
