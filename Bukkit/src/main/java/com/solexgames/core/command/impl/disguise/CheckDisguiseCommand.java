package com.solexgames.core.command.impl.disguise;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Command(label = "checkdisguise", permission = "scandium.command.checkdisguise")
public class CheckDisguiseCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player>");
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
                final boolean disguised = potPlayer.isDisguised();

                if (disguised) {
                    sender.sendMessage(potPlayer.getOriginalRankColor() + potPlayer.getOriginalName() + Color.SECONDARY_COLOR + " is disguised as " + Color.translate(potPlayer.getDisguiseRank().getColor()) + potPlayer.getName() + Color.SECONDARY_COLOR + " with the rank " + Color.translate(potPlayer.getDisguiseRank().getColor() + potPlayer.getDisguiseRank().getName()) + Color.SECONDARY_COLOR + ".");
                } else {
                    sender.sendMessage(target.getDisplayName() + ChatColor.RED + " is not disguised.");
                }
            }
        }

        return false;
    }
}
