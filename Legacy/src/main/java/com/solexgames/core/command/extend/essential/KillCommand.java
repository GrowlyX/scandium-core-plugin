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

public class KillCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        ChatColor mainColor = network.getMainColor();
        ChatColor secondColor = network.getSecondaryColor();
        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.kill")) {
            if (args.length == 0) {
                player.setHealth(0);
                player.sendMessage(Color.translate("&aKilled yourself."));

                StaffUtil.sendAlert(player, "killed themself");
            }
            if (args.length > 0) {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target != null) {
                    target.setHealth(0);
                    player.sendMessage(Color.translate(secondColor + "Killed " + target.getDisplayName() + secondColor + "."));

                    StaffUtil.sendAlert(player, "killed " + target.getName());
                } else {
                    player.sendMessage(Color.translate("&cThat player does not exist."));
                }
            }
        } else {
            player.sendMessage(NO_PERMISSION);
        }
        return false;
    }
}
