package com.solexgames.core.command.impl.disguise;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.disguise.DisguiseData;
import com.solexgames.core.player.PotPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class DisguiseCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.disguise")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (potPlayer.isDisguised()) {
            player.sendMessage(ChatColor.RED + "You're already disguised with the name " + ChatColor.YELLOW + potPlayer.getName() + ChatColor.RED + "!");
            return false;
        }

        final DisguiseData disguiseData = CorePlugin.getInstance().getDisguiseCache().getRandomData();
        final DisguiseData skinData = CorePlugin.getInstance().getDisguiseCache().getRandomData();

        if (disguiseData != null && skinData != null) {
            CorePlugin.getInstance().getDisguiseManager().disguise(player, disguiseData, skinData);
        } else {
            player.sendMessage(ChatColor.RED + "There aren't any available disguises!");
        }

        return false;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("nick");
    }
}
