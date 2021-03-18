package com.solexgames.core.command.extend.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GmspCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.gmsp")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(Color.translate("&aSet your gamemode to Spectator."));

            StaffUtil.sendAlert(player, "set gamemode spectator");
        }
        if (args.length > 0) {
            Player target = Bukkit.getPlayerExact(args[0]);

            if (target != null) {
                target.setGameMode(GameMode.SPECTATOR);
                player.sendMessage(Color.translate("&aSet " + target.getDisplayName() + "'s&a gamemode to Spectator."));

                StaffUtil.sendAlert(player, "set gamemode spectator for " + target.getName());
            } else {
                player.sendMessage(Color.translate("&cThat player does not exist."));
            }
        }
        return false;
    }
}
