package com.solexgames.core.command.extend.toggle;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleStaffMessagesCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (player.hasPermission("scandium.staff")) {
            if (potPlayer.isCanSeeStaffMessages()) {
                player.sendMessage(Color.translate("&cYou have disabled staff messages."));
                potPlayer.setCanSeeStaffMessages(false);
            } else {
                player.sendMessage(Color.translate("&aYou can now see staff messages."));
                potPlayer.setCanSeeStaffMessages(true);
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }

        return false;
    }
}
