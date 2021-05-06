package com.solexgames.core.command.impl.auth;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.LockedState;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Command(label = "auth", aliases = {"2fa"})
public class AuthCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (!player.hasPermission("scandium.2fa")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (!LockedState.isLocked(player)) {
            player.sendMessage(ChatColor.RED + "I'm sorry, but you cannot perform this action right now.");
            return false;
        }

        final String input = StringUtils.join(args).replace(" ", "");

        int code;
        try {
            code = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "I'm sorry, but the code you've entered is invalid.");
            return false;
        }

        CompletableFuture.runAsync(() -> {
            boolean valid = potPlayer.isAuthValid(code);

            if (valid) {
                LockedState.release(player);

                player.sendMessage(ChatColor.GREEN + "You've verified your identity and have been unlocked.");
                player.sendMessage(ChatColor.GREEN + "Thank you for keeping your account and our server safe!");
            } else {
                player.sendMessage(ChatColor.RED + "I'm sorry, but we couldn't verify your identity. Maybe check the code you entered and try again?");
            }
        });

        return true;
    }
}
