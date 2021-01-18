package vip.potclub.core.command.extend.essential;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.util.Color;

public class GmsCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("core.command.gms")) {
            if (args.length == 0) {
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(Color.translate("&aSet your gamemode to Survival."));
            }
            if (args.length > 0) {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target != null) {
                    target.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(Color.translate("&aSet " + target.getDisplayName() + "'s&a gamemode to Survival."));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
