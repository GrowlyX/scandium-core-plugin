package com.solexgames.core.command.impl.toggle;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class TogglePrivateMessagesCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (potPlayer.isCanReceiveDms()) {
            player.sendMessage(ChatColor.RED + ("You've disabled private messages."));
            potPlayer.setCanSeeStaffMessages(false);
        } else {
            player.sendMessage(ChatColor.GREEN + Color.translate("You can now see private messages."));
            potPlayer.setCanSeeStaffMessages(true);
        }

        return false;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("tpm");
    }

    @Override
    public boolean isHidden() {
        return false;
    }
}
