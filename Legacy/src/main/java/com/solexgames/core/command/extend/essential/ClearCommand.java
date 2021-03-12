package com.solexgames.core.command.extend.essential;

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

public class ClearCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        ChatColor secondColor = network.getSecondaryColor();
        Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.clear")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            player.getInventory().clear();
            player.sendMessage(Color.translate(secondColor + "Cleared your inventory."));

            StaffUtil.sendAlert(player, "cleared");
        }
        if (args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target != null) {
                player.getInventory().clear();

                player.sendMessage(Color.translate(secondColor + "Cleared " + target.getDisplayName() + secondColor + "'s inventory."));

                StaffUtil.sendAlert(player, "cleared " + target.getName());
            } else {
                player.sendMessage(Color.translate("&cThat player does not exist."));
            }
        }
        return false;
    }
}
