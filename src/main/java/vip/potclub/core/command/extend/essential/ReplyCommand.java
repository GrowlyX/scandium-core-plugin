package vip.potclub.core.command.extend.essential;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.StringUtil;

public class ReplyCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        if (args.length == 0) {
            player.sendMessage(Color.translate("&cUsage: /" + label + " <message>."));
        }
        if (args.length > 0) {
            String message = StringUtil.buildMessage(args, 0);
            if (potPlayer.isCanReceiveDms()) {
                if (potPlayer.getLastRecipient() != null) {
                    if (potPlayer.getLastRecipient().isOnline()) {
                        StringUtil.sendPrivateMessage(player, potPlayer.getLastRecipient(), message);
                    } else {
                        player.sendMessage(Color.translate("&cThat player is not online."));
                    }
                } else {
                    player.sendMessage(Color.translate("&cYou don't have an ongoing conversation with anyone."));
                }
            } else {
                player.sendMessage(Color.translate("&cYou have your dms disabled."));
            }
        }
        return false;
    }
}
