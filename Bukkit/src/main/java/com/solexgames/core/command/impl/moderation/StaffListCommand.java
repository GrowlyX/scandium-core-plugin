package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;

/**
 * @author GrowlyX
 * @since 5/26/2021
 */

@Command(label = "stafflist", aliases = "sl", permission = "scandium.staff")
public class StaffListCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        sender.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 53));

        CorePlugin.getInstance().getRankManager().getSortedRanks().stream()
                .filter(rank -> rank.getPermissions().contains("scandium.staff"))
                .forEach(rank -> {
                    sender.sendMessage(Color.translate(rank.getColor() + rank.getItalic()) + ChatColor.BOLD.toString() + rank.getName() + ":");

                    CorePlugin.getInstance().getPlayerManager().getAllNetworkProfiles().stream()
                            .filter(networkPlayer -> networkPlayer.getRankName().equals(rank.getName()))
                            .map(networkPlayer -> ChatColor.GRAY + " * " + Color.SECONDARY_COLOR + networkPlayer.getName() + ChatColor.GRAY + " (" + DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - networkPlayer.getConnectionTime(), true, true) + ") (" + networkPlayer.getServerName() + ")")
                            .collect(Collectors.toList())
                            .forEach(sender::sendMessage);

                    sender.sendMessage(" ");
                });

        sender.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 53));

        return false;
    }
}
