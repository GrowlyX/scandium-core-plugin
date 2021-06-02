package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.manager.PlayerManager;
import com.solexgames.core.manager.ServerManager;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(label = "vanish", permission = "scandium.command.vanish", aliases = "v")
public class VanishCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            this.toggleVanish(player, 0);
        }

        if (args.length == 1) {
            try {
                final int power = Integer.parseInt(args[0]);
                this.toggleVanish(player, power);

                return false;
            } catch (Exception ignored) { }

            if (!player.hasPermission("scandium.command.vanish.other")) {
                player.sendMessage(NO_PERMISSION);
                return false;
            }

            final Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                final boolean vanished = CorePlugin.getInstance().getServerManager().getVanishedPlayers().contains(target);
                this.toggleVanish(target, 0);

                player.sendMessage(ChatColor.DARK_AQUA + "[S] " + (vanished ? ChatColor.AQUA + "You've unvanished " + target.getDisplayName() + ChatColor.AQUA + "." : ChatColor.AQUA + "You've vanished " + target.getDisplayName() + ChatColor.AQUA + " with a power of " + ChatColor.DARK_AQUA + 0 + ChatColor.AQUA + "."));
            } else {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
            }
        }

        if (args.length == 2) {
            if (!player.hasPermission("scandium.command.vanish.other")) {
                player.sendMessage(NO_PERMISSION);
                return false;
            }

            try {
                final Player target = Bukkit.getPlayer(args[0]);
                final int power = Integer.parseInt(args[1]);

                if (target != null) {
                    final boolean vanished = CorePlugin.getInstance().getServerManager().getVanishedPlayers().contains(target);
                    this.toggleVanish(target, power);

                    player.sendMessage(ChatColor.DARK_AQUA + "[S] " + (vanished ? ChatColor.AQUA + "You've unvanished " + target.getDisplayName() + ChatColor.AQUA + "." : ChatColor.AQUA + "You've vanished " + target.getDisplayName() + ChatColor.AQUA + " with a power of " + ChatColor.DARK_AQUA + power + ChatColor.AQUA + "."));
                } else {
                    player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
                }
            } catch (Exception ignored) {
                player.sendMessage(ChatColor.RED + "Error: That's not a valid integer (power).");
            }
        }

        return false;
    }

    public void toggleVanish(Player target, int power) {
        final PlayerManager vanishManager = CorePlugin.getInstance().getPlayerManager();
        final ServerManager serverManager = CorePlugin.getInstance().getServerManager();

        final boolean vanished = serverManager.getVanishedPlayers().contains(target);
        final Runnable runnable = vanished ? () -> vanishManager.unVanishPlayer(target) : () -> vanishManager.vanishPlayer(target, power);

        runnable.run();
    }
}
