package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.manager.PlayerManager;
import com.solexgames.core.player.global.NetworkPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AltsCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (!sender.hasPermission("scandium.command.alts")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player>.");
        }
        if (args.length == 1) {
            final String target = args[0];
            final NetworkPlayer targetPlayer = CorePlugin.getInstance().getPlayerManager().getNetworkPlayer(target);

            if (targetPlayer != null) {
                final String playerFormattedDisplay = (Rank.getByName(targetPlayer.getRankName()) != null ? Color.translate(Rank.getByName(targetPlayer.getRankName()).getColor()) : ChatColor.GREEN.toString()) + targetPlayer.getName();
                final PlayerManager manager = CorePlugin.getInstance().getPlayerManager();

                sender.sendMessage(new String[] {
                        "",
                        playerFormattedDisplay + serverType.getSecondaryColor() + "'s Alt Accounts " + ChatColor.GRAY + "(x" + manager.getAlts(targetPlayer) + ")" + serverType.getSecondaryColor() + ":",
                        manager.getAltsMessage(targetPlayer),
                        ""
                });
            } else {
                sender.sendMessage(ChatColor.RED + "That player is not online the network right now.");
            }


        }
        return false;
    }
}
