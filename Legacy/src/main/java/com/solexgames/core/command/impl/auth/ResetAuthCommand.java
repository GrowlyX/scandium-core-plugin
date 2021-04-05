package com.solexgames.core.command.impl.auth;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetAuthCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("scandium.2fa.reset")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player>.");
        } else {
            Player player = Bukkit.getPlayerExact(args[0]);

            if (player != null) {
                PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

                potPlayer.setVerify(false);
                potPlayer.setSetupSecurity(true);
                potPlayer.setKey(null);
                potPlayer.setHasSetup2FA(false);

                sender.sendMessage(ChatColor.GREEN + "Reset that player's 2FA.");
            } else {
                sender.sendMessage(ChatColor.RED + "That player does not exist.");
            }
        }
        return false;
    }
}
