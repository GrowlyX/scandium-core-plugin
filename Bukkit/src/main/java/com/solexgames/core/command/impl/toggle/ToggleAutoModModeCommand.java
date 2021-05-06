package com.solexgames.core.command.impl.toggle;

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

@Command(label = "toggleautomodmode", aliases = "tamod")
public class ToggleAutoModModeCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (!player.hasPermission("scandium.staff")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (potPlayer.isAutoModMode()) {
            player.sendMessage(ChatColor.RED + ("You've disabled auto mod mode."));
            potPlayer.setAutoModMode(false);
        } else {
            player.sendMessage(ChatColor.GREEN + Color.translate("You've enabled auto mod mode."));
            potPlayer.setAutoModMode(true);
        }

        return false;
    }
}
