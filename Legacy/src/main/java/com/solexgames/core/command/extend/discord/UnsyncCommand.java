package com.solexgames.core.command.extend.discord;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnsyncCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
            if (potPlayer.isSynced() && (potPlayer.getSyncDiscord() != null)) {
                potPlayer.setSynced(false);
                potPlayer.setSyncDiscord(null);

                player.sendMessage(Color.translate("&aUn-synced your account!"));
            } else {
                player.sendMessage(Color.translate("&cYou are not synced to a discord account."));
            }
        }
        return false;
    }
}
