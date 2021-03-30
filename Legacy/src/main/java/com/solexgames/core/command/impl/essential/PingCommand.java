package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(CorePlugin.getInstance().getServerManager().getNetwork().getSecondaryColor() + "Your ping is: " + CorePlugin.getInstance().getServerManager().getNetwork().getMainColor() + StaffUtil.getPing(player));
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);

            if (target != null) {
                player.sendMessage(CorePlugin.getInstance().getServerManager().getNetwork().getSecondaryColor() + target.getName() + "'s ping is: " + CorePlugin.getInstance().getServerManager().getNetwork().getMainColor() + StaffUtil.getPing(target));
            } else {
                player.sendMessage(ChatColor.RED + "That player does not exist.");
            }
        }
        return false;
    }
}
