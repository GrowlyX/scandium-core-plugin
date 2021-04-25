package com.solexgames.core.command.impl.other;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.PotPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NametagCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.nametag")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

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

    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }
}
