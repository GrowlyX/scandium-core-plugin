package com.solexgames.core.command.impl.disguise;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.disguise.DisguiseData;
import com.solexgames.core.player.PotPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Command(label = "undisguise", aliases = {"unnick"})
public class UnDisguiseCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.undisguise")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (!potPlayer.isDisguised()) {
            player.sendMessage(ChatColor.RED + "You aren't currently disguised!");
            return false;
        }

        CorePlugin.getInstance().getDisguiseManager().unDisguise(potPlayer, player);

        return false;
    }
}
