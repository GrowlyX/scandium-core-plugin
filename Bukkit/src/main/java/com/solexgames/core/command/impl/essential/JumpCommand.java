package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.global.NetworkPlayer;
import com.solexgames.core.util.BungeeUtil;
import com.solexgames.core.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GrowlyX
 * @since 5/14/2021
 */

@Command(label = "jump", aliases = "jtp")
public class JumpCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.jump")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player>.");
        }
        if (args.length == 1) {
            final NetworkPlayer networkPlayer = CorePlugin.getInstance().getPlayerManager().getNetworkPlayer(args[0]);

            if (networkPlayer != null) {
                player.sendMessage(Color.SECONDARY_COLOR + "You're being jumped to " + Color.MAIN_COLOR + networkPlayer.getName() + Color.SECONDARY_COLOR + "'s server...");

                BungeeUtil.sendToServer(player, networkPlayer.getServerName(), CorePlugin.getInstance());
                CorePlugin.getInstance().getPlayerManager().sendToNetworkStaffFormatted(player.getDisplayName() + "&b has jumped to &a" + networkPlayer.getName() + "&b.");
            } else {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
            }
        }

        return false;
    }
}
