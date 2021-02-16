package vip.potclub.core.command.extend.essential;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.util.Color;

public class FlyCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.fly")) {
            if (args.length == 0) {
                if (player.isFlying()) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.sendMessage(Color.translate("&cDisabled your flight."));
                } else {
                    player.setAllowFlight(true);
                    player.setFlying(true);
                    player.sendMessage(Color.translate("&aEnabled your flight."));
                }
            }
            if (args.length > 0) {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target != null) {
                    if (target.isFlying()) {
                        target.setFlying(false);
                        target.sendMessage(Color.translate("&cDisabled " + target.getDisplayName() + "&c's flight."));
                    } else {
                        target.setFlying(true);
                        target.sendMessage(Color.translate("&aEnabled " + target.getDisplayName() + "&a's flight."));
                    }
                } else {
                    player.sendMessage(Color.translate("&cThat player does not exist."));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
