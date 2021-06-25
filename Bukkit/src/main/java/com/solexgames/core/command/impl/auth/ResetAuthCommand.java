package com.solexgames.core.command.impl.auth;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.Constants;
import com.solexgames.core.util.LockedState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GrowlyX
 * @since 6/24/2021
 */

@Command(
        label = "authreset",
        permission = "scandium.command.authreset",
        consoleOnly = true
)
public class ResetAuthCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player>");
            return false;
        }

        final Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Error: A player by that name couldn't be found.");
            return true;
        }

        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

        potPlayer.setAuthSecret(null);
        potPlayer.setHasSetup2FA(false);
        potPlayer.setAuthBypassed(false);
        potPlayer.setLastAuth(-1L);
        potPlayer.saveWithoutRemove();

        sender.sendMessage(Constants.STAFF_PREFIX + ChatColor.GREEN + "You've reset " + target.getDisplayName() + ChatColor.GREEN + "'s 2FA.");

        return true;
    }
}
