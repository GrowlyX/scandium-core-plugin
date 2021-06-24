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

import java.util.Arrays;
import java.util.List;

@Command(
        label = "authbypass",
        permission = "scandium.command.authbypass",
        aliases = {"remove2fa", "2fabypass"},
        consoleOnly = true
)
public class AuthBypassCommand extends BaseCommand {

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

        if (!potPlayer.isAuthBypassed()) {
            target.sendMessage(Constants.STAFF_PREFIX + ChatColor.GREEN + "An administrator has granted you two-factor authentication bypass.");
            sender.sendMessage(Constants.STAFF_PREFIX + ChatColor.YELLOW + "You've just granted " + ChatColor.AQUA + target.getName() + ChatColor.YELLOW + " two-factor authentication bypass.");
        } else {
            target.sendMessage(Constants.STAFF_PREFIX + ChatColor.GREEN + "An administrator has removed your two-factor authentication bypass.");
            sender.sendMessage(Constants.STAFF_PREFIX + ChatColor.YELLOW + "You've just removed " + ChatColor.AQUA + target.getName() + ChatColor.YELLOW + "'s two-factor authentication bypass.");
        }

        potPlayer.saveWithoutRemove();
        potPlayer.setAuthBypassed(!potPlayer.isAuthBypassed());

        LockedState.release(target);

        return true;
    }
}
