package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(label = "reply", aliases = "r", hidden = false)
public class ReplyCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (potPlayer.getLastRecipient() == null) {
            player.sendMessage(ChatColor.RED + ("You aren't in a conversation with anyone."));
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(Color.SECONDARY_COLOR + "You're currently messaging " + Color.MAIN_COLOR + potPlayer.getLastRecipient() + Color.SECONDARY_COLOR + ".");
        }

        if (args.length > 0) {
            final String message = StringUtil.buildMessage(args, 0);
            final PotPlayer potTarget = CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getLastRecipient());

            if (potTarget == null) {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
                return false;
            }

            if (Bukkit.getPlayer(potPlayer.getLastRecipient()) == null) {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
                return false;
            }

            if (potTarget.isVanished() || potTarget.isDisguised() && (potPlayer.getActiveGrant().getRank().getWeight() < potTarget.getActiveGrant().getRank().getWeight())) {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
                return false;
            }

            if (!potTarget.isIgnoring(potPlayer.getPlayer())) {
                player.sendMessage(ChatColor.RED + ("Error: That player is currently ignoring you."));
                return false;
            }

            if (!potPlayer.isIgnoring(potTarget.getPlayer())) {
                player.sendMessage(ChatColor.RED + ("Error: You are currently ignoring that player."));
                return false;
            }

            if (!potPlayer.isCanReceiveDms()) {
                player.sendMessage(ChatColor.RED + ("Error: You've your dms disabled."));
                return false;
            }

            if (potTarget.isCurrentlyRestricted()) {
                player.sendMessage(ChatColor.RED + ("Error: You cannot message this player right now."));
                return false;
            }

            if (potTarget.isCurrentlyMuted()) {
                player.sendMessage(ChatColor.RED + ("Error: You cannot message this player right now."));
                return false;
            }

            if (!potTarget.isCanReceiveDms()) {
                player.sendMessage(ChatColor.RED + ("Error: That player has their dms disabled."));
                return false;
            }

            if (CorePlugin.getInstance().getFilterManager().isDmFiltered(player, potTarget.getName(), message)) {
                player.sendMessage(ChatColor.RED + ("Error: You cannot use censored words in a direct message."));
                return false;
            }

            StringUtil.sendPrivateMessage(player, potTarget.getPlayer(), message);
        }

        return false;
    }
}
