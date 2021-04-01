package com.solexgames.core.command.impl.library;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

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
            String target = args[0];
            CompletableFuture<PotPlayer> completableFuture = new CompletableFuture<>();

            sender.sendMessage(serverType.getSecondaryColor() + "Searching for that player...");

            CompletableFuture.runAsync(() -> completableFuture.complete(CorePlugin.getInstance().getPlayerManager().getPlayer(target)));

            completableFuture.thenAccept(networkPlayer -> {
                if (networkPlayer == null) {
                    sender.sendMessage(ChatColor.RED + "I'm sorry, but we could not find that player on the network.");
                } else {
                    sender.sendMessage(networkPlayer.getColorByRankColor() + networkPlayer.getName() + ChatColor.GREEN + " is disguised as " + Color.translate(networkPlayer.getDisguiseRank().getColor() + networkPlayer.getDisguiseRank().getName()) + org.bukkit.ChatColor.GREEN + "!");
                }
            });
        }
        return false;
    }
}
