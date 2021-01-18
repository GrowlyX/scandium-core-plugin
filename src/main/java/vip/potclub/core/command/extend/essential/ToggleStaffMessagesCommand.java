package vip.potclub.core.command.extend.essential;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;

public class ToggleStaffMessagesCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        PotPlayer potPlayer = PotPlayer.getPlayer(player);

        if (player.hasPermission("core.staff")) {
            if (potPlayer.isCanSeeStaffMessages()) {
                player.sendMessage(Color.translate("&cYou have disabled staff messages."));
                potPlayer.setCanSeeStaffMessages(false);
            } else {
                player.sendMessage(Color.translate("&aYou can now see staff messages."));
                potPlayer.setCanSeeStaffMessages(true);
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }

        return false;
    }
}
