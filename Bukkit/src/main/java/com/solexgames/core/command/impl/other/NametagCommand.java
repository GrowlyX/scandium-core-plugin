package com.solexgames.core.command.impl.other;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.PotPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(label = "nametag", permission = "scandium.command.nametag")
public class NametagCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

            if (potPlayer.getRainbowNametag().isActive()) {
                player.sendMessage(ChatColor.RED + "You've enabled the rainbow nametag.");
            } else {
                player.sendMessage(ChatColor.GREEN + "You've disabled the rainbow nametag.");
            }

            potPlayer.getRainbowNametag().toggle();
        }

        return false;
    }
}
