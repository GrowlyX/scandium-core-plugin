package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.global.NetworkPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author GrowlyX
 * @since 5/26/2021
 */

@Command(label = "stafflist", aliases = "sl", permission = "scandium.staff")
public class StaffListCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        final List<Rank> ranksWithStaffPermissions = CorePlugin.getInstance().getRankManager().getSortedRanks().stream()
                .filter(rank -> rank.getPermissions().contains("scandium.staff"))
                .collect(Collectors.toList());

        if (ranksWithStaffPermissions.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "I'm sorry, but there aren't any staff ranks available.");
            return false;
        }

        sender.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 53));
        sender.sendMessage(Color.MAIN_COLOR + ChatColor.BOLD + "Online Staff:");
        sender.sendMessage(" ");

        ranksWithStaffPermissions.forEach(rank -> {
            final List<NetworkPlayer> playersWithSpecifiedRank = CorePlugin.getInstance().getPlayerManager().getAllNetworkProfiles().stream()
                    .filter(networkPlayer -> networkPlayer.getRankName().equals(rank.getName()))
                    .collect(Collectors.toList());

            if (playersWithSpecifiedRank.isEmpty()) {
                return;
            }

            sender.sendMessage(Color.translate(rank.getColor() + rank.getItalic()) + ChatColor.BOLD.toString() + rank.getName() + ":");

            playersWithSpecifiedRank.stream()
                    .map(networkPlayer -> ChatColor.GRAY + " * " + Color.SECONDARY_COLOR + networkPlayer.getName() + ChatColor.GRAY + "(" + CorePlugin.FORMAT.format(new Date(networkPlayer.getConnectionTime())) + ") (" + DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - networkPlayer.getConnectionTime(), true, true) + ") (" + networkPlayer.getServerName() + ")")
                    .collect(Collectors.toList())
                    .forEach(sender::sendMessage);

            sender.sendMessage(" ");
        });

        sender.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 53));

        return false;
    }
}
