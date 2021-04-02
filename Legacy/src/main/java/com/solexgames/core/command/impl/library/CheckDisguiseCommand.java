package com.solexgames.core.command.impl.library;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class CheckDisguiseCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (!sender.hasPermission("scandium.command.checkdisguise")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player>.");
        }

        if (args.length > 0) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                return false;
            }

            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

            if (potPlayer == null) {
                sender.sendMessage(ChatColor.RED + "I'm sorry, but we could not find that player on the network.");
            } else {
                sender.sendMessage(potPlayer.getColorByRankColor() + potPlayer.getName() + ChatColor.GREEN + " is disguised as " + Color.translate(potPlayer.getDisguiseRank().getColor() + potPlayer.getDisguiseRank().getName()) + org.bukkit.ChatColor.GREEN + "!");
            }
        }
        return false;
    }
}
