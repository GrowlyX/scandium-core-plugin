package vip.potclub.core.command.extend.essential;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;

public class ToggleTipsCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (potPlayer.isCanSeeTips()) {
            player.sendMessage(Color.translate("&cYou have disabled tip broadcasts."));
            potPlayer.setCanSeeTips(false);
        } else {
            player.sendMessage(Color.translate("&aYou can now see tip broadcasts."));
            potPlayer.setCanSeeTips(true);
        }

        return false;
    }
}
