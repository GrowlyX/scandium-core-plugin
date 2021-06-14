package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.Constants;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Command(label = "socialspy", permission = "scandium.command.socialspy")
public class SocialSpyCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (potPlayer.isSocialSpy()) {
            player.sendMessage(Constants.STAFF_PREFIX + ChatColor.GREEN + "You're now viewing other users' private messages.");
            potPlayer.setSocialSpy(false);
        } else {
            player.sendMessage(Constants.STAFF_PREFIX + ChatColor.RED + "You've stopped viewing other users' private messages.");
            potPlayer.setSocialSpy(true);
        }

        return false;
    }
}
