package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.global.NetworkPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;

public class FindCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (!sender.hasPermission("scandium.command.find")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player>.");
        }

        if (args.length > 0) {
            String target = args[0];
            CompletableFuture<NetworkPlayer> completableFuture = new CompletableFuture<>();

            sender.sendMessage(serverType.getSecondaryColor() + "Searching for that player...");

            CompletableFuture.runAsync(() -> {
                NetworkPlayer networkPlayer = CorePlugin.getInstance().getPlayerManager().getNetworkPlayer(target);
                completableFuture.complete(networkPlayer);
            });

            completableFuture.thenAccept(networkPlayer -> {
                if (networkPlayer == null) {
                    sender.sendMessage(ChatColor.RED + "I'm sorry, but we could not find that player on the network.");
                } else {
                    Rank rank = Rank.getByName(networkPlayer.getRankName());
                    String displayName = Color.translate((rank != null ? rank.getColor() : ChatColor.GRAY) + networkPlayer.getName());

                    sender.sendMessage(serverType.getMainColor() + displayName + serverType.getSecondaryColor() + " is currently online " + serverType.getMainColor() + networkPlayer.getServerName() + serverType.getSecondaryColor() + "!");
                }
            });
        }
        return false;
    }
}
