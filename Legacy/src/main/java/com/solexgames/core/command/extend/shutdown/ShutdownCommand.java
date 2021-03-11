package com.solexgames.core.command.extend.shutdown;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.manager.ShutdownManager;
import com.solexgames.core.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShutdownCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        ShutdownManager shutdownManager = CorePlugin.getInstance().getShutdownManager();
        if (player.hasPermission("scandium.command.shutdown")) {
            if (args.length == 0) {
                ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
                sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <start|cancel>."));
            }
            if (args.length > 0) {
                switch (args[0]) {
                    case "start":
                        if (!shutdownManager.isShutdownScheduled()) {
                            shutdownManager.initiateShutdown(60, player);
                            player.sendMessage(Color.translate("&aInitiated a shutdown in &660 seconds&a."));
                        } else {
                            player.sendMessage(Color.translate("&cThere's already a shutdown initiated!"));
                        }
                        break;
                    case "cancel":
                        if (shutdownManager.isShutdownScheduled()) {
                            shutdownManager.stopShutdown(player);
                            player.sendMessage(Color.translate("&aCancelled the scheduled shutdown."));
                        } else {
                            player.sendMessage(Color.translate("&cThere aren't any shutdowns scheduled!"));
                        }
                        break;
                    default:
                        player.sendMessage(Color.translate("&cUsage: /" + label + " <start|cancel>"));
                        break;
                }
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}
