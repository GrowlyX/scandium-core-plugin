package vip.potclub.core.command.extend.modsuite;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.manager.PlayerManager;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;

public class StaffModeCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        PlayerManager playerManager = CorePlugin.getInstance().getPlayerManager();
        PotPlayer potPlayer = PotPlayer.getPlayer(player);

        if (player.hasPermission("scandium.command.modmode")) {
            if (args.length == 0) {
                if (potPlayer.isStaffMode()) {
                    playerManager.unModModePlayer(player);
                } else {
                    playerManager.modModePlayer(player);
                }
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}

