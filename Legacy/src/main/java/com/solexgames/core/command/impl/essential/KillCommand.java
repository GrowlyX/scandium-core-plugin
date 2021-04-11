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

public class KillCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.kill")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            player.setHealth(0);
            player.sendMessage(Color.SECONDARY_COLOR + ("You've killed yourself."));

            StaffUtil.sendAlert(player, "killed themself");
        }
        if (args.length > 0) {
            Player target = Bukkit.getPlayerExact(args[0]);

            if (target != null) {
                target.setHealth(0);
                player.sendMessage(Color.SECONDARY_COLOR + "Killed " + target.getDisplayName() + Color.SECONDARY_COLOR + ".");

                StaffUtil.sendAlert(player, "killed " + target.getName());
            } else {
                player.sendMessage(ChatColor.RED + ("Error: That player does not exist."));
            }
        }

        return false;
    }
}
