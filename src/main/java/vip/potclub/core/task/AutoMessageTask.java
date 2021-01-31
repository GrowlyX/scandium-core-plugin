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

    private final String tipPrefix;
    private final boolean padding;
    private final boolean prefix;

    public AutoMessageTask() {
        this.tipPrefix = Color.translate(CorePlugin.getInstance().getConfig().getString("tips.prefix"));
        this.padding = CorePlugin.getInstance().getConfig().getBoolean("tips.padding");
        this.prefix = CorePlugin.getInstance().getConfig().getBoolean("tips.useprefix");

        setupMessages();
        runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 120 * 20L);
    }

    @Override
    public void run() {
        sendMessage(defaultMessages);
    }

    private void setupMessages() {
        defaultMessages.addAll(CorePlugin.getInstance().getConfig().getStringList("tips.messages"));
    }

    private void sendMessage(List<String> input) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            PotPlayer potPlayer = PotPlayer.getPlayer(player);
            if (potPlayer.isCanSeeTips()) {
                if (padding) player.sendMessage("  ");
                int count = CorePlugin.RANDOM.nextInt(defaultMessages.size());
                player.sendMessage(Color.translate((this.prefix ? this.tipPrefix : "") + input.get(this.lastCount == count ? CorePlugin.RANDOM.nextInt(input.size()) : count)));
                this.lastCount = count;
                if (padding) player.sendMessage("  ");
            }
        });
    }
}
