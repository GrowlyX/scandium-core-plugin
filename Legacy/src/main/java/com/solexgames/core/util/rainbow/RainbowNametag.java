package com.solexgames.core.util.rainbow;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * An object to setup rainbow nametags for users.
 * <p>
 *
 * @author GrowlyX
 * @since 4/12/2021
 */

@Getter
public class RainbowNametag extends BukkitRunnable {

    private final Player player;
    private final CorePlugin plugin;

    private final ChatColor[] colors = new ChatColor[]{
            ChatColor.DARK_RED, ChatColor.RED, ChatColor.GOLD,
            ChatColor.YELLOW, ChatColor.GREEN, ChatColor.DARK_GREEN,
            ChatColor.AQUA, ChatColor.DARK_AQUA, ChatColor.BLUE, ChatColor.DARK_BLUE
    };

    private ChatColor currentColor = colors[0];
    private boolean active = false;

    /**
     * Constructor to make a new rainbow
     *
     * @param player the player the nametag is for
     */
    public RainbowNametag(Player player, CorePlugin plugin) {
        this.player = player;
        this.plugin = plugin;
    }

    /**
     * Toggle the status of the runnable
     */
    public void toggle() {
        if (this.active) {
            this.active = false;
            this.currentColor = this.colors[0];

            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

            potPlayer.setupPlayerTag();
            potPlayer.setupPlayerList();

            this.cancel();
        } else {
            this.active = true;

            this.runTaskTimer(plugin, 0L, 35L);
        }
    }

    @Override
    public void run() {
        final int ordinal = this.currentColor.ordinal();
        final ChatColor color = (this.currentColor = colors[ordinal >= colors.length ? 0 : ordinal + 1]);

        this.player.setPlayerListName(color + this.player.getName());

        for (Player target : Bukkit.getOnlinePlayers()) {
            this.plugin.getNameTagManager().setupNameTag(this.player, target, color);
        }
    }
}
