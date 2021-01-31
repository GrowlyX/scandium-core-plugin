package vip.potclub.core.command.extend.discord;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;

public class UnsyncCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            PotPlayer potPlayer = PotPlayer.getPlayer(player);
            if (potPlayer.isSynced() && (potPlayer.getSyncDiscord() != null)) {
                potPlayer.setSynced(false);
                potPlayer.setSyncDiscord(null);

                player.sendMessage(Color.translate("&aUnsynced your account!"));
            } else {
                player.sendMessage(Color.translate("&cYou are not synced to a discord account."));
            }
        }
        return false;
    }
}
