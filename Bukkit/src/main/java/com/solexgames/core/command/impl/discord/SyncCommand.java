package com.solexgames.core.command.impl.discord;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SyncCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!Bukkit.getPluginManager().isPluginEnabled("Indium")) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        player.sendMessage(new String[]{
                Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Discord Syncing",
                Color.SECONDARY_COLOR + "To sync your account with your discord account and receive",
                Color.SECONDARY_COLOR + "the " + ChatColor.GREEN + "Verified" + Color.SECONDARY_COLOR + " role, copy this code '" + Color.MAIN_COLOR + potPlayer.getSyncCode() + Color.SECONDARY_COLOR + "' and",
                Color.SECONDARY_COLOR + "paste it into the " + Color.MAIN_COLOR + "#sync" + Color.SECONDARY_COLOR + " channel with " + Color.MAIN_COLOR + "'-sync'" + Color.SECONDARY_COLOR + " on our discord",
                Color.SECONDARY_COLOR + "server! To join our discord, use " + Color.MAIN_COLOR + CorePlugin.getInstance().getServerManager().getNetwork().getDiscordLink() + Color.SECONDARY_COLOR + "!",
        });

        return false;
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }

    @Override
    public boolean isHidden() {
        return !Bukkit.getPluginManager().isPluginEnabled("Indium");
    }
}
