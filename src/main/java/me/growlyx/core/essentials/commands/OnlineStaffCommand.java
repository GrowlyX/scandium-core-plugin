package me.growlyx.core.essentials.commands;

import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

import java.util.Iterator;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "onlinestaff", permission = "core.staff"))
public class OnlineStaffCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;

        if (args.length == 0) {

            if (player.hasPermission("core.staff")) {

                player.sendMessage(CC.translate("&7&m-------------------------------------------"));
                player.sendMessage(CC.translate(Messages.string("MESSAGES.ONLINE-STAFF")));

                Iterator var8 = Bukkit.getOnlinePlayers().iterator();

                while(var8.hasNext()) {

                    Player staff = (Player) var8.next();

                    if (staff != null && staff.hasPermission("core.staff")) {
                        player.sendMessage(staff.getName());
                    }

                }

                player.sendMessage(CC.translate("&7&m-------------------------------------------"));
            }


        return false;
    }

    return true;
    }
}
