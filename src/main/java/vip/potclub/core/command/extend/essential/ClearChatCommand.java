package vip.potclub.core.command.extend.essential;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.util.Color;

public class ClearChatCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.mutechat")) {
            if (args.length == 0) {
                for (int lines = 0; lines < 250; lines++) {
                    Bukkit.broadcastMessage(Color.translate("  "));
                }
                Bukkit.broadcastMessage(Color.translate("&aThe chat has been cleared by " + player.getDisplayName() + "&a."));
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
