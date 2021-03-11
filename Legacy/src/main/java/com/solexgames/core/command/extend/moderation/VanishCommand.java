package com.solexgames.core.command.extend.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.manager.PlayerManager;
import com.solexgames.core.manager.ServerManager;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        PlayerManager vanishManager = CorePlugin.getInstance().getPlayerManager();
        ServerManager manager = CorePlugin.getInstance().getServerManager();

        if (player.hasPermission("scandium.command.vanish")) {
            if (args.length == 0) {
                if (manager.getVanishedPlayers().contains(player)) {
                    vanishManager.unVanishPlayer(player);

                    StaffUtil.sendAlert(player, "unvanished");
                } else {
                    vanishManager.vanishPlayer(player);

                    StaffUtil.sendAlert(player, "vanished");
                }
            }
            if (args.length > 0) {
                if (player.hasPermission("scandium.command.vanish.other")) {
                    Player target = Bukkit.getPlayerExact(args[0]);
                    if (target != null) {
                        if (manager.getVanishedPlayers().contains(target)) {
                            vanishManager.unVanishPlayer(target);
                            player.sendMessage(Color.translate("&aUnvanished " + target.getName() + "."));

                            StaffUtil.sendAlert(player, "unvanished " + target.getName());
                        } else {
                            vanishManager.vanishPlayer(target);
                            player.sendMessage(Color.translate("&aVanished " + target.getName() + "."));

                            StaffUtil.sendAlert(player, "vanished " + target.getName());
                        }
                    } else {
                        player.sendMessage(Color.translate("&cThat player does not exist."));
                    }
                } else {
                    player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}
