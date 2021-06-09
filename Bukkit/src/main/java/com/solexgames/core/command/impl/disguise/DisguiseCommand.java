package com.solexgames.core.command.impl.disguise;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.disguise.DisguiseData;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Command(label = "disguise", permission = "scandium.command.disguise", aliases = {"nick"})
public class DisguiseCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (potPlayer.isDisguised()) {
            player.sendMessage(ChatColor.RED + "You're already disguised with the name " + ChatColor.YELLOW + potPlayer.getName() + ChatColor.RED + "!");
            return false;
        }

        final DisguiseData disguiseData = CorePlugin.getInstance().getDisguiseCache().getRandomData();
        final DisguiseData skinData = CorePlugin.getInstance().getDisguiseCache().getRandomData();

        if (disguiseData != null && skinData != null) {
            CorePlugin.getInstance().getDisguiseManager().disguise(player, disguiseData, skinData, Rank.getDefault());
        } else {
            player.sendMessage(ChatColor.RED + "Something went wrong while trying to disguise you.");
        }

        return false;
    }
}
