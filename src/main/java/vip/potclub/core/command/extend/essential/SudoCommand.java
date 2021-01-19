package vip.potclub.core.command.extend.essential;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.StringUtil;

public class SudoCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("core.command.sudo")) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <player> <message>."));
            }
            if (args.length > 0) {
                if (args.length == 1) {
                    player.sendMessage(Color.translate("&cUsage: /" + label + " <player> <message>."));
                }
                if (args.length > 1) {
                    Player target = Bukkit.getPlayerExact(args[0]);
                    String message = StringUtil.buildMessage(args, 1);
                    if (target != null) {
                        target.chat(message);
                        player.sendMessage(Color.translate("&aMade " + target.getDisplayName() + "&a chat '" + message + "&a'."));
                    } else {
                        player.sendMessage(Color.translate("&cThat player is not online."));
                    }
                }
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
