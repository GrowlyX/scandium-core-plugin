package vip.potclub.core.command.extend.essential;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.StringUtil;

public class MessageCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
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
                    PotPlayer potPlayer = PotPlayer.getPlayer(target);
                    if (potPlayer.isCanReceiveDms()) {
                        StringUtil.sendPrivateMessage(player, target, message);

                        PotPlayer potPerson = PotPlayer.getPlayer(player);
                        potPerson.setLastRecipient(target);

                        potPlayer.setLastRecipient(player);
                    } else {
                        player.sendMessage(Color.translate("&cThat player has their dms disabled."));
                    }
                } else {
                    player.sendMessage(Color.translate("&cThat player does not exist."));
                }
            }
        }
        return false;
    }
}
