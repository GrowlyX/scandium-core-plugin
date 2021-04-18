package com.solexgames.core.command.impl.other;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.PotPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NametagCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("scandium.command.nametag")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

            if (potPlayer.getRainbowNametag().isActive()) {
                player.sendMessage(ChatColor.RED + "Stopped the rainbow nametag!");
            } else {
                player.sendMessage(ChatColor.GREEN + "Started the rainbow nametag!");
            }

            potPlayer.getRainbowNametag().toggle();
        }

        return false;
    }
}
