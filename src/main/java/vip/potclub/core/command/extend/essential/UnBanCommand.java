package vip.potclub.core.command.extend.essential;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;

public class UnBanCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.unban")) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <player>."));
            }
            if (args.length > 0) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                if (offlinePlayer != null) {
                    PotPlayer potPlayer = PotPlayer.getPlayer(offlinePlayer.getPlayer());
                    if (potPlayer == null) {
                        PotPlayer newPotPlayer = new PotPlayer(offlinePlayer.getUniqueId());
                        if (newPotPlayer.isBanned()) {
                            newPotPlayer.unBanPlayer();
                            player.sendMessage(Color.translate("&aUnbanned that player."));
                        } else {
                            player.sendMessage(Color.translate("&cThat player is not banned."));
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
