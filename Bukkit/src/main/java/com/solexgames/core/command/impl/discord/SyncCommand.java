package com.solexgames.core.command.impl.discord;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command(label = "sync", hidden = false)
public class SyncCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!Bukkit.getPluginManager().isPluginEnabled("Indium")) {
            player.sendMessage(ChatColor.RED + "Error: This command is disabled on this server.");
            return false;
        }

        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (!potPlayer.isSynced()) {
            player.sendMessage(new String[]{
                    Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Discord Syncing",
                    Color.SECONDARY_COLOR + "To sync your account with your discord account and receive",
                    Color.SECONDARY_COLOR + "the " + ChatColor.GREEN + "Verified" + Color.SECONDARY_COLOR + " role, copy this code '" + Color.MAIN_COLOR + potPlayer.getSyncCode() + Color.SECONDARY_COLOR + "' and",
                    Color.SECONDARY_COLOR + "paste it into the " + Color.MAIN_COLOR + "#sync" + Color.SECONDARY_COLOR + " channel with " + Color.MAIN_COLOR + "'-sync'" + Color.SECONDARY_COLOR + " on our discord",
                    Color.SECONDARY_COLOR + "server! To join our discord, use " + Color.MAIN_COLOR + CorePlugin.getInstance().getServerManager().getNetwork().getDiscordLink() + Color.SECONDARY_COLOR + "!",
            });
        } else {
            player.sendMessage(ChatColor.RED + "Error: You are already synced to the discord account " + ChatColor.YELLOW + potPlayer.getSyncDiscord() + ChatColor.RED + "!");
        }

        return false;
    }
}
