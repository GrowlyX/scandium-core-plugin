package vip.potclub.core.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;

import java.util.ArrayList;
import java.util.List;

public class AutoMessageTask extends BukkitRunnable {

    private final List<String> defaultMessages = new ArrayList<>();

    private final String tipPrefix;
    private final boolean padding;
    private final boolean prefix;

    private int lastCount;

    public AutoMessageTask() {
        this.tipPrefix = Color.translate(CorePlugin.getInstance().getConfig().getString("tips.prefix"));
        this.padding = CorePlugin.getInstance().getConfig().getBoolean("tips.padding");
        this.prefix = CorePlugin.getInstance().getConfig().getBoolean("tips.use-prefix");

        this.defaultMessages.addAll(CorePlugin.getInstance().getConfig().getStringList("tips.messages"));

        runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, CorePlugin.getInstance().getConfig().getInt("tips.interval") * 20L);
    }

    @Override
    public void run() {
        sendMessage(defaultMessages);
    }

    private void sendMessage(List<String> input) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            PotPlayer potPlayer = PotPlayer.getPlayer(player);
            if (potPlayer.isCanSeeTips()) {
                if (padding) player.sendMessage("  ");
                int count = CorePlugin.RANDOM.nextInt(defaultMessages.size());
                player.sendMessage(Color.translate((this.prefix ? this.tipPrefix : "") + input.get(this.lastCount == count ? CorePlugin.RANDOM.nextInt(input.size()) : count).replace("<nl>", "\n")));
                this.lastCount = count;
                if (padding) player.sendMessage("  ");
            }
        });
    }
}
