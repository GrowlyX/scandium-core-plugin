package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StaffUtil;
import com.solexgames.core.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        ChatColor mainColor = serverType.getMainColor();
        ChatColor secondColor = serverType.getSecondaryColor();

        if (!sender.hasPermission("scandium.command.sudo")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length <= 1) {
            sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " [c:] [e:] <message>."));
        }

        if (args.length > 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            String message = StringUtil.buildMessage(args, 1);

            if (target != null) {
                if (message.startsWith("c:")) {
                    target.chat(message.replace("c:", ""));
                    sender.sendMessage(Color.translate(secondColor + "Made " + target.getDisplayName() + secondColor + " chat '" + mainColor + message.replace("c:", "") + secondColor + "'."));
                } else if (message.startsWith("e:")) {
                    target.performCommand(message.replace("e:", ""));
                    sender.sendMessage(Color.translate(secondColor + "Made " + target.getDisplayName() + secondColor + " execute '" + mainColor + message.replace("e:", "") + secondColor + "'."));
                } else {
                    target.chat(message);
                    sender.sendMessage(Color.translate(secondColor + "Made " + target.getDisplayName() + secondColor + " chat '" + mainColor + message + secondColor + "'."));
                }

                if (sender instanceof Player) {
                    StaffUtil.sendAlert((Player) sender, "sudoed " + target.getName());
                }
            } else {
                sender.sendMessage(Color.translate("&cThat player does not exist."));
            }
        }
        return false;
    }
}
