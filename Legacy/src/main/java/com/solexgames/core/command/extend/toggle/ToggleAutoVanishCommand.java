package com.solexgames.core.command.extend.toggle;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleAutoVanishCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (player.hasPermission("scandium.staff")) {
            if (potPlayer.isAutoVanish()) {
                player.sendMessage(Color.translate("&cYou have disabled auto vanish."));
                potPlayer.setAutoVanish(false);
            } else {
                player.sendMessage(Color.translate("&aYou have enabled auto vanish."));
                potPlayer.setAutoVanish(true);
            }
        } else {
            player.sendMessage(NO_PERMISSION);
        }

        return false;
    }
}
