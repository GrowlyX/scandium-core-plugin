package com.solexgames.core.command.impl.auth;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.LockedState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AuthBypassCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + "I'm sorry, but this command can only be executed via console.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "A player by that name couldn't be found.");
            return true;
        }

        if (!LockedState.isLocked(target)) {
            sender.sendMessage(ChatColor.RED + "That player does not have to by bypassed.");
            return true;
        }

        LockedState.release(target);
        target.sendMessage(ChatColor.GREEN + "An administrator has granted you Two-Factor Authentication bypass.");

        sender.sendMessage(ChatColor.GREEN + "You've just granted " + target.getName() + " 2FA bypass.");

        return true;
    }
}
