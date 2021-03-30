package com.solexgames.core.command.impl.toggle;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleTipsCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
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
