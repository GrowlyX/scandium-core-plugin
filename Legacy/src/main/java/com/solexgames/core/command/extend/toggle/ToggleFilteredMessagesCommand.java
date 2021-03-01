package com.solexgames.core.command.extend.toggle;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleFilteredMessagesCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (player.hasPermission("scandium.staff")) {
            if (potPlayer.isCanSeeFiltered()) {
                player.sendMessage(Color.translate("&cYou have disabled filtered messages."));
                potPlayer.setCanSeeFiltered(false);
            } else {
                player.sendMessage(Color.translate("&aYou can now see filtered messages."));
                potPlayer.setCanSeeFiltered(true);
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }

        return false;
    }
}