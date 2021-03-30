package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.fly")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            if (player.isFlying()) {
                player.setAllowFlight(false);
                player.setFlying(false);
                player.sendMessage(Color.translate("&cDisabled your flight."));

                StaffUtil.sendAlert(player, "disabled flight");
            } else {
                player.setAllowFlight(true);
                player.setFlying(true);
                player.sendMessage(Color.translate("&aEnabled your flight."));

                StaffUtil.sendAlert(player, "enabled flight");
            }
        }
        if (args.length > 0) {
            Player target = Bukkit.getPlayerExact(args[0]);

            if (target == null) {
                player.sendMessage(Color.translate("&cThat player does not exist."));
            } else {
                if (target.isFlying()) {
                    target.setFlying(false);
                    target.sendMessage(Color.translate("&cDisabled " + target.getDisplayName() + "&c's flight."));

                    StaffUtil.sendAlert(player, "disabled flight for " + target.getName());
                } else {
                    target.setFlying(true);
                    target.sendMessage(Color.translate("&aEnabled " + target.getDisplayName() + "&a's flight."));

                    StaffUtil.sendAlert(player, "enabled flight for " + target.getName());
                }
            }
        }
        return false;
    }
}
