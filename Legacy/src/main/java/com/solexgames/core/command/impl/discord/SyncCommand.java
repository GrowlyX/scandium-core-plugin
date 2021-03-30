package com.solexgames.core.command.impl.discord;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SyncCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Bukkit.getPluginManager().isPluginEnabled("dRobot")) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;

        ChatColor MC = CorePlugin.getInstance().getServerManager().getNetwork().getMainColor();
        String MCB = CorePlugin.getInstance().getServerManager().getNetwork().getMainColor() + ChatColor.BOLD.toString();
        ChatColor SC = CorePlugin.getInstance().getServerManager().getNetwork().getSecondaryColor();

        player.sendMessage(Color.translate("  "));
        player.sendMessage(Color.translate(MCB + "Discord Sync:"));
        player.sendMessage(Color.translate("  "));
        player.sendMessage(Color.translate(SC + "To sync your account with your discord account and receive"));
        player.sendMessage(Color.translate(SC + "the " + ChatColor.GREEN + "Verified" + SC + " role, copy this code '" + MC + CorePlugin.getInstance().getPlayerManager().getPlayer(player).getSyncCode() + SC + "' and"));
        player.sendMessage(Color.translate(SC + "paste it into the " + MC + "#sync" + SC + " channel with " + MC + "'-sync'" + SC + " on our discord"));
        player.sendMessage(Color.translate(SC + "server! To join our discord, use " + MC + CorePlugin.getInstance().getServerManager().getNetwork().getDiscordLink() + SC + "!"));
        player.sendMessage(Color.translate("  "));

        return false;
    }
}
