package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SocialSpyCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (!player.hasPermission("scandium.command.socialspy")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (potPlayer.isAutoVanish()) {
            player.sendMessage(Color.translate("&cYou've disabled social spy."));
            potPlayer.setSocialSpy(false);
        } else {
            player.sendMessage(Color.translate("&aYou've enabled social spy."));
            potPlayer.setSocialSpy(true);
        }

        return false;
    }
}
