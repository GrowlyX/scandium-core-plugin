package com.solexgames.core.command.impl.syncing;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(label = "unsync")
public class UnSyncCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (potPlayer.isSynced()) {
            potPlayer.setSynced(false);
            potPlayer.setSyncDiscord(null);

            player.sendMessage(Color.SECONDARY_COLOR + "You've un-synced your " + ChatColor.BLUE + "Discord" + Color.SECONDARY_COLOR + " account.");
            player.sendMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + "To re-sync, please follow the instructions provided when doing /sync!");
        } else {
            player.sendMessage(ChatColor.RED + "Error: You are not synced to a discord account.");
        }

        return false;
    }
}
