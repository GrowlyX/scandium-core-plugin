package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.manager.PlayerManager;
import com.solexgames.core.player.PotPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffModeCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        PlayerManager playerManager = CorePlugin.getInstance().getPlayerManager();
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

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
