package vip.potclub.core.command.extend.essential;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;

public class UnMuteCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.unmute")) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <player>."));
            }
            if (args.length > 0) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                if (offlinePlayer != null) {
                    PotPlayer potPlayer = PotPlayer.getPlayer(offlinePlayer.getPlayer());
                    if (potPlayer == null) {
                        PotPlayer newPotPlayer = new PotPlayer(offlinePlayer.getUniqueId());
                        if (newPotPlayer.isMuted()) {
                            newPotPlayer.unMutePlayer();
                            player.sendMessage(Color.translate("&aUnmuted that player."));
                        } else {
                            player.sendMessage(Color.translate("&cThat player is not muted."));
                        }
                    }
                } else {
                    player.sendMessage(Color.translate("&cThat player does not exist."));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
