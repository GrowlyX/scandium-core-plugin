package vip.potclub.core.command.extend.essential;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.menu.extend.ReportMenu;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;
import vip.potclub.core.util.StringUtil;

public class ReportCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            System.out.println("die");
            return false;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage(Color.translate("&cUsage: /" + label + " <player>."));
        }

        if (args.length > 0) {
            if (args.length == 1) {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target != null) {
                    if (target.getUniqueId() != player.getUniqueId()) {
                        new ReportMenu(player, target).open(player);
                    } else {
                        player.sendMessage(Color.translate("&cYou cannot report yourself!"));
                    }
                } else {
                    player.sendMessage(Color.translate("&cThat player does not exist."));
                }
            }
        }
        return false;
    }
}
