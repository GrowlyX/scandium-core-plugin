package vip.potclub.core.command.extend.essential;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.menu.extend.media.ExternalMediaMenu;
import vip.potclub.core.menu.extend.media.MediaMenu;
import vip.potclub.core.util.Color;

public class MediaCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            new MediaMenu(player).open(player);
        }
        if (args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target != null) {
                new ExternalMediaMenu(target).open(player);
            } else {
                player.sendMessage(Color.translate("&cThat player does not exist."));
            }
        }
        return false;
    }
}
