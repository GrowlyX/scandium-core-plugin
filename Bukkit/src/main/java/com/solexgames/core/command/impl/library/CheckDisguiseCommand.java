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
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("scandium.command.checkdisguise")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player>.");
        }

        if (args.length > 0) {
            final Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                return false;
            }

            final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

            if (potPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Error: That player does not exist.");
            } else {
                if (potPlayer.getDisguiseRank() != null) {
                    sender.sendMessage(potPlayer.getColorByRankColor() + potPlayer.getName() + Color.SECONDARY_COLOR + " is disguised as " + Color.translate(potPlayer.getDisguiseRank().getColor() + potPlayer.getDisguiseRank().getName()) + Color.SECONDARY_COLOR + "!");
                } else {
                    sender.sendMessage(ChatColor.RED + "Error: That player is not disguised.");
                }
            }
        }

        return false;
    }
}
