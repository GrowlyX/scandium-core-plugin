package com.solexgames.core.command.extend.essential;

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
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        ChatColor mainColor = network.getMainColor();
        ChatColor secondColor = network.getSecondaryColor();
        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.sudo")) {
            ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
            if (args.length == 0) {
                player.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " [c:] [e:] <message>."));
            }
            if (args.length > 0) {
                if (args.length == 1) {
                    player.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " [c:] [e:] <message>."));
                }
                if (args.length > 1) {
                    Player target = Bukkit.getPlayerExact(args[0]);
                    String message = StringUtil.buildMessage(args, 1);
                    if (target != null) {
                        if (message.startsWith("c:")) {
                            target.chat(message.replace("c:", ""));
                            player.sendMessage(Color.translate(secondColor + "Made " + target.getDisplayName() + secondColor + " chat '" + mainColor + message.replace("c:", "") + secondColor + "'."));
                        } else if (message.startsWith("e:")) {
                            target.performCommand(message.replace("e:", ""));
                            player.sendMessage(Color.translate(secondColor + "Made " + target.getDisplayName() + secondColor + " execute '" + mainColor + message.replace("e:", "") + secondColor + "'."));
                        } else {
                            target.chat(message);
                            player.sendMessage(Color.translate(secondColor + "Made " + target.getDisplayName() + secondColor + " chat '" + mainColor + message + secondColor + "'."));
                        }

                        StaffUtil.sendAlert(player, "sudoed " + target.getName());
                    } else {
                        player.sendMessage(Color.translate("&cThat player does not exist."));
                    }
                }
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}
