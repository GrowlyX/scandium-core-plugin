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
import java.util.Arrays;
import java.util.List;

public class UnSyncCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!Bukkit.getPluginManager().isPluginEnabled("dRobot")) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

            if (potPlayer.isSynced() && (potPlayer.getSyncDiscord() != null)) {
                potPlayer.setSynced(false);
                potPlayer.setSyncDiscord(null);

                player.sendMessage(ChatColor.GREEN + Color.translate("Un-synced your account!"));
            } else {
                player.sendMessage(ChatColor.RED + ("Error: You are not synced to a discord account."));
            }
        }

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
