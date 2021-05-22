package com.solexgames.core.command.impl.auth;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.LockedState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Command(label = "authbypass", aliases = {"remove2fa", "2fabypass"})
public class AuthBypassCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + "Only console can execute this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player>");
            return false;
        }

        final Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Error: A player by that name couldn't be found.");
            return true;
        }

        if (!LockedState.isLocked(target)) {
            sender.sendMessage(ChatColor.RED + "Error: That player does not have to be bypassed.");
            return true;
        }

        LockedState.release(target);
        target.sendMessage(ChatColor.DARK_AQUA + "[2FA]" + ChatColor.GREEN + " An administrator has granted you two-factor authentication bypass for this log on session.");

        sender.sendMessage(ChatColor.DARK_AQUA + "[2FA]" + ChatColor.YELLOW + " You've just granted " + ChatColor.AQUA + target.getName() + ChatColor.YELLOW + " two-factor authentication bypass.");

        return true;
    }
}
