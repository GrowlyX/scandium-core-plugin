package com.solexgames.core.command.extend.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoAllCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.sudoall")) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <message>."));
            }

            if (args.length > 0) {
                String message = StringUtil.buildMessage(args, 0);
                Bukkit.getOnlinePlayers().forEach(player1 -> player1.chat(message));
                player.sendMessage(Color.translate("&aMade all online players chat '" + message + "&a'."));
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
