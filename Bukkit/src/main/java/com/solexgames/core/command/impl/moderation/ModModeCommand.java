package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.manager.PlayerManager;
import com.solexgames.core.player.PotPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModModeCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final PlayerManager playerManager = CorePlugin.getInstance().getPlayerManager();
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (!player.hasPermission("scandium.command.modmode")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (potPlayer.isStaffMode()) {
            playerManager.unModModePlayer(player);
        } else {
            playerManager.modModePlayer(player);
        }

        return false;
    }
}

