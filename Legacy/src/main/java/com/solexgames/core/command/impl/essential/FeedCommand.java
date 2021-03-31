package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.feed")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            player.setFoodLevel(20);
            player.sendMessage(Color.translate(network.getSecondaryColor() + "Set your food level to " + network.getMainColor() + "20" + network.getSecondaryColor() +"."));

            StaffUtil.sendAlert(player, "fed");
        }

        if (args.length > 0) {
            Player target = Bukkit.getPlayerExact(args[0]);

            if (target == null) {
                player.sendMessage(Color.translate("&cThat player does not exist."));
            } else {
                target.setFoodLevel(20);
                player.sendMessage(Color.translate(network.getSecondaryColor() + "Set " + target.getDisplayName() + " food level to " + network.getMainColor() + "20" + network.getSecondaryColor() +"."));

                StaffUtil.sendAlert(player, "fed " + target.getName());
            }
        }
        return false;
    }
}