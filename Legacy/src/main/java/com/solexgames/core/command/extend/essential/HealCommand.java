package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.server.Network;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Network network = CorePlugin.getInstance().getServerManager().getNetwork();
        ChatColor mainColor = network.getMainColor();
        ChatColor secondColor = network.getSecondaryColor();
        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.heal")) {
            if (args.length == 0) {
                player.setHealth(20);
                player.sendMessage(Color.translate(secondColor + "Set your health level to " + mainColor + "20" + secondColor +"."));

                StaffUtil.sendAlert(player, "fed");
            }
            if (args.length > 0) {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target != null) {
                    target.setHealth(20);
                    player.sendMessage(Color.translate(secondColor + "Set " + target.getDisplayName() + " health level to " + mainColor + "20" + secondColor +"."));

                    StaffUtil.sendAlert(player, "fed " + target.getName());
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
