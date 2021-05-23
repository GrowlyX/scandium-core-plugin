package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import com.solexgames.core.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(label = "sudo", permission = "scandium.command.sudo")
public class SudoCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("scandium.command.sudo")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length <= 1) {
            sender.sendMessage(Color.translate(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " [c:] [e:] <message>"));
        }

        if (args.length > 1) {
            final Player target = Bukkit.getPlayer(args[0]);
            final String message = StringUtil.buildMessage(args, 1);

            if (target != null) {
                if (message.startsWith("c:")) {
                    target.chat(message.replace("c:", ""));
                    sender.sendMessage(Color.SECONDARY_COLOR + "Made " + target.getDisplayName() + Color.SECONDARY_COLOR + " chat '" + Color.MAIN_COLOR + message.replace("c:", "") + Color.SECONDARY_COLOR + "'.");
                } else if (message.startsWith("e:")) {
                    target.performCommand(message.replace("e:", ""));
                    sender.sendMessage(Color.SECONDARY_COLOR + "Made " + target.getDisplayName() + Color.SECONDARY_COLOR + " execute '" + Color.MAIN_COLOR + message.replace("e:", "") + Color.SECONDARY_COLOR + "'.");
                } else {
                    target.chat(message);
                    sender.sendMessage(Color.SECONDARY_COLOR + "Made " + target.getDisplayName() + Color.SECONDARY_COLOR + " chat '" + Color.MAIN_COLOR + message + Color.SECONDARY_COLOR + "'.");
                }

                if (sender instanceof Player) {
                    final Player player = (Player) sender;

                    PlayerUtil.sendAlert(player, "sudoed " + target.getName());
                }
            } else {
                sender.sendMessage(ChatColor.RED + "That player does not exist");
            }
        }
        return false;
    }
}
