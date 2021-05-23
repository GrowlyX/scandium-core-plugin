package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
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
            player.sendMessage(ChatColor.RED + "You've disabled social spy.");
            potPlayer.setSocialSpy(false);
        } else {
            player.sendMessage(ChatColor.GREEN + "You've enabled social spy.");
            potPlayer.setSocialSpy(true);
        }

        return false;
    }
}
