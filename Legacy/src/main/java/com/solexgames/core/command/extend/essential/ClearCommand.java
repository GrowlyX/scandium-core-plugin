package com.solexgames.core.command.extend.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.clear")) {
            if (args.length == 0) {
                player.getInventory().clear();
                player.sendMessage(Color.translate("&aCleared your inventory."));

                StaffUtil.sendAlert(player, "cleared");
            }
            if (args.length > 0) {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target != null) {
                    player.getInventory().clear();
                    player.sendMessage(Color.translate("&aCleared " + target.getDisplayName() + "&a's inventory."));

                    StaffUtil.sendAlert(player, "cleared " + target.getName());
                } else {
                    player.sendMessage(Color.translate("&cThat player does not exist."));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}