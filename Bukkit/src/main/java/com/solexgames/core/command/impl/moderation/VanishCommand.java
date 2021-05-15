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

@Command(label = "vanish", aliases = "v")
public class VanishCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final PlayerManager vanishManager = CorePlugin.getInstance().getPlayerManager();
        final ServerManager manager = CorePlugin.getInstance().getServerManager();

        if (!player.hasPermission("scandium.command.vanish")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            if (manager.getVanishedPlayers().contains(player)) {
                vanishManager.unVanishPlayer(player);

                PlayerUtil.sendAlert(player, "unvanished");
            } else {
                vanishManager.vanishPlayer(player);

                PlayerUtil.sendAlert(player, "vanished");
            }
        }
        if (args.length == 1) {
            if (!player.hasPermission("scandium.command.vanish.other")) {
                player.sendMessage(NO_PERMISSION);
                return false;
            }

            final Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                if (manager.getVanishedPlayers().contains(target)) {
                    vanishManager.unVanishPlayer(target);
                    player.sendMessage(ChatColor.GREEN + Color.translate("Unvanished " + target.getName() + "."));

                    PlayerUtil.sendAlert(player, "unvanished " + target.getName());
                } else {
                    vanishManager.vanishPlayer(target);
                    player.sendMessage(ChatColor.GREEN + Color.translate("Vanished " + target.getName() + "."));

                    PlayerUtil.sendAlert(player, "vanished " + target.getName());
                }
            } else {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
            }
        }

        return false;
    }
}
