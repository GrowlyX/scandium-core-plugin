package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MessageCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (args.length < 1) {
            player.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player> <message>."));
        }
        if (args.length > 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            String message = StringUtil.buildMessage(args, 1);

            if (target == null) {
                player.sendMessage(Color.translate("&cThat player does not exist."));
                return false;
            }

            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
            PotPlayer potTarget = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

            if (potTarget != null) {
                player.sendMessage(Color.translate("&cThat player does not exist."));
                return false;
            }

            if (!potPlayer.getLastRecipient().isOnline()) {
                player.sendMessage(Color.translate("&cThat player does not exist."));
                return false;
            }
            if (potTarget.isVanished() && (potPlayer.getActiveGrant().getRank().getWeight() < potTarget.getActiveGrant().getRank().getWeight())) {
                player.sendMessage(Color.translate("&cThat player does not exist."));
                return false;
            }
            if (potTarget.isIgnoring(potPlayer.getPlayer())) {
                player.sendMessage(Color.translate("&cThat player is currently ignoring you."));
                return false;
            }
            if (potPlayer.isIgnoring(potTarget.getPlayer())) {
                player.sendMessage(Color.translate("&cYou are currently ignoring that player."));
                return false;
            }
            if (!potPlayer.isCanReceiveDms()) {
                player.sendMessage(Color.translate("&cYou have your dms disabled."));
                return false;
            }
            if (potTarget.isCurrentlyRestricted()) {
                player.sendMessage(Color.translate("&cYou cannot message this player right now."));
                return false;
            }
            if (potTarget.isCurrentlyMuted()) {
                player.sendMessage(Color.translate("&cYou cannot message this player right now."));
                return false;
            }
            if (!potTarget.isCanReceiveDms()) {
                player.sendMessage(Color.translate("&cThat player has their dms disabled."));
                return false;
            }
            if (CorePlugin.getInstance().getFilterManager().isDmFiltered(player, potPlayer.getLastRecipient().getName(), message)) {
                player.sendMessage(Color.translate("&cYou cannot use censored words in a direct message."));
                return false;
            }

            StringUtil.sendPrivateMessage(player, target, message);

            potPlayer.setLastRecipient(target);
            potTarget.setLastRecipient(player);
        }

        return false;
    }
}
