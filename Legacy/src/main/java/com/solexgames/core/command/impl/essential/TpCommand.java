package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        ChatColor secondColor = serverType.getSecondaryColor();
        Player player = (Player) sender;

        if (!sender.hasPermission("scandium.command.tp")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player>."));
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);

            if (target != null) {
                player.teleport(target.getLocation());
                player.sendMessage(Color.translate(secondColor + "Teleported you to " + target.getDisplayName() + secondColor + "."));

                StaffUtil.sendAlert(player, "teleported to " + target.getName());
            } else {
                player.sendMessage(Color.translate("&cThat player does not exist."));
            }
        }
        return false;
    }
}
