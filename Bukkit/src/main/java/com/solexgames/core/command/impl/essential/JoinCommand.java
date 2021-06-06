package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.global.NetworkPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.server.NetworkServer;
import com.solexgames.core.util.BungeeUtil;
import com.solexgames.core.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GrowlyX
 * @since 6/5/2021
 */

@Command(label = "join")
public class JoinCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <server>");
        }
        if (args.length == 1) {
            final NetworkServer networkServer = NetworkServer.getByName(args[0]);

            if (networkServer == null) {
                player.sendMessage(ChatColor.RED + "Error: There is no online server named " + ChatColor.RED + args[0] + ChatColor.RED + ".");
                return false;
            }

            player.sendMessage(Color.SECONDARY_COLOR + "Joining " + Color.MAIN_COLOR + networkServer.getServerName() + Color.SECONDARY_COLOR + "...");

            BungeeUtil.sendToServer(player, networkServer.getServerName(), CorePlugin.getInstance());
        }

        return false;
    }
}
