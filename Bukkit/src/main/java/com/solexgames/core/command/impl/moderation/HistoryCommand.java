package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.menu.impl.punish.history.PunishHistoryViewMainMenu;
import com.solexgames.core.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@Command(label = "history", aliases = "c")
public class HistoryCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.history")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player>.");
        }
        if (args.length == 1) {
            CompletableFuture.supplyAsync(() -> CorePlugin.getInstance().getUuidCache().getUuidFromUsername(args[0]))
                    .thenAccept(uuid -> {
                        player.sendMessage(Color.SECONDARY_COLOR + "Viewing punishment history for: " + Color.MAIN_COLOR + args[0]);

                        new PunishHistoryViewMainMenu(player, uuid, args[0]).open(player);
                    });
        }

        return false;
    }
}
