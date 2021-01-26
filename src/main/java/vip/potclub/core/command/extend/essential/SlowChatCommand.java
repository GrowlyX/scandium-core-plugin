package vip.potclub.core.command.extend.essential;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.util.Color;

public class SlowChatCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.slowchat")) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <time> &7(Use 0 to disable slow chat)."));
            }
            if (args.length == 1) {
                try {
                    int time = Integer.parseInt(args[0]);
                    CorePlugin.getInstance().getServerManager().setChatSlow(time * 1000L);
                    Bukkit.broadcastMessage(CorePlugin.getInstance().getServerManager().getChatSlow() > 0L ? ChatColor.GREEN + "Public chat is now in slow mode. " + ChatColor.GRAY + "(" + time + " seconds)" : ChatColor.RED + "Public chat is no longer in slow mode.");
                } catch (NumberFormatException e) {
                    player.sendMessage(Color.translate("&cThat number is invalid."));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
