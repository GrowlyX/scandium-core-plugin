package com.solexgames.core.command.impl.toggle;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleTipsCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (potPlayer.isCanSeeTips()) {
            player.sendMessage(ChatColor.RED + ("You've disabled tip broadcasts."));
            potPlayer.setCanSeeTips(false);
        } else {
            player.sendMessage(ChatColor.GREEN + Color.translate("You can now see tip broadcasts."));
            potPlayer.setCanSeeTips(true);
        }

        return false;
    }
}
