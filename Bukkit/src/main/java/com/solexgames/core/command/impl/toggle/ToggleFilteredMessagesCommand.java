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

@Command(label = "togglefilteredmessages", permission = "scandium.staff", aliases = "tfm")
public class ToggleFilteredMessagesCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (potPlayer.isCanSeeFiltered()) {
            player.sendMessage(ChatColor.RED + ("You've disabled filtered messages."));
            potPlayer.setCanSeeFiltered(false);
        } else {
            player.sendMessage(ChatColor.GREEN + Color.translate("You can now see filtered messages."));
            potPlayer.setCanSeeFiltered(true);
        }

        return false;
    }
}
