package com.solexgames.core.command.impl.shutdown;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.manager.ShutdownManager;
import com.solexgames.core.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ShutdownCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final ShutdownManager shutdownManager = CorePlugin.getInstance().getShutdownManager();

        if (!player.hasPermission("scandium.command.shutdown")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            this.getHelpMessage(1, player,
                    "/shutdown <start>",
                    "/shutdown <cancel>"
            );
        }
        if (args.length == 1) {
            switch (args[0]) {
                case "start":
                    if (!shutdownManager.isShutdownScheduled()) {
                        shutdownManager.initiateShutdown(60, player);
                        player.sendMessage(ChatColor.GREEN + Color.translate("Initiated a shutdown in &660 seconds&a."));
                    } else {
                        player.sendMessage(ChatColor.RED + ("Error: There's already a shutdown initiated!"));
                    }
                    break;
                case "cancel":
                    if (shutdownManager.isShutdownScheduled()) {
                        shutdownManager.stopShutdown(player);
                        player.sendMessage(ChatColor.GREEN + Color.translate("Cancelled the scheduled shutdown."));
                    } else {
                        player.sendMessage(ChatColor.RED + ("Error: There aren't any shutdowns scheduled!"));
                    }
                    break;
                default:
                    sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <start|cancel>.");
                    break;
            }
        }
        return false;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("delaystop");
    }
}
