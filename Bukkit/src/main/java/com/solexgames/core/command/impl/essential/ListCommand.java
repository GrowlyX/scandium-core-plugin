package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Command(label = "list", aliases = {"who", "online"}, hidden = false)
public class ListCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        final List<Player> playerList = new ArrayList<>(this.getOnlinePlayers(false, false));
        final boolean split = Bukkit.getOnlinePlayers().size() > 350;

        if (sender instanceof ConsoleCommandSender) {
            final String ranks = CorePlugin.getInstance().getRankManager().getRanks().stream()
                    .sorted(Comparator.comparingInt(rank -> -rank.getWeight()))
                    .map(rank -> Color.translate((rank.isHidden() ? "&7*" : "") + rank.getColor() + rank.getItalic() + rank.getName()))
                    .collect(Collectors.joining(ChatColor.WHITE + ", "));
            final String players = playerList.stream()
                    .map(player -> CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId()))
                    .sorted(Comparator.comparingInt(potPlayer -> -(potPlayer.getDisguiseRank() != null ? potPlayer.getDisguiseRank().getWeight() : potPlayer.getActiveGrant().getRank().getWeight())))
                    .map(potPlayer -> Color.translate((potPlayer.isStaffMode() ? "&7[S] " : "") + (potPlayer.isVanished() ? "&7[V] " : "") + potPlayer.getActiveGrant().getRank().getColor() + potPlayer.getName()))
                    .collect(Collectors.joining(ChatColor.WHITE + ", "));

            sender.sendMessage(new String[]{
                    ranks,
                    Color.translate("&f(" + playerList.size() + "/" + Bukkit.getMaxPlayers() + ") ") + players
            });

            return false;
        }

        final Player player = (Player) sender;
        final String ranks = CorePlugin.getInstance().getRankManager().getRanks().stream()
                .filter(rank -> !rank.isHidden())
                .sorted(Comparator.comparingInt(rank -> -rank.getWeight()))
                .map(rank -> Color.translate(rank.getColor() + rank.getItalic() + rank.getName().replace("-", " ")))
                .collect(Collectors.joining(ChatColor.WHITE + ", "));
        final String players = this.getOnlinePlayers(!player.hasPermission("scandium.staff"), split).stream()
                .map(player1 -> CorePlugin.getInstance().getPlayerManager().getPlayer(player1.getUniqueId()))
                .sorted(Comparator.comparingInt(potPlayer -> -(potPlayer.getDisguiseRank() != null ? potPlayer.getDisguiseRank().getWeight() : potPlayer.getActiveGrant().getRank().getWeight())))
                .map(potPlayer -> this.getFormattedName(potPlayer, player))
                .collect(Collectors.joining(ChatColor.WHITE + ", "));

        sender.sendMessage(new String[]{
                ranks,
                Color.translate("&f(" + this.getOnlinePlayers(true, split).size() + "/" + Bukkit.getMaxPlayers() + "): ") + players
        });

        if (split) {
            sender.sendMessage(ChatColor.RED + "(Only displaying the first " + ChatColor.BOLD + "350 players" + ChatColor.RED + ")");
        }

        return false;
    }

    private String getFormattedName(PotPlayer potPlayer, Player viewer) {
        return Color.translate((viewer.hasPermission("scandium.staff") ? (potPlayer.isStaffMode() ? "&7[S] " : "") + (potPlayer.isVanished() ? "&7[V] " : "") : "") + (potPlayer.getDisguiseRank() != null ? potPlayer.getDisguiseRank().getColor() : potPlayer.getActiveGrant().getRank().getColor()) + potPlayer.getName());
    }

    private Collection<Player> getOnlinePlayers(boolean filter, boolean split) {
        final List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());

        if (!filter) {
            return split ? playerList.subList(0, 350) : playerList;
        } else {
            final List<Player> finalList = playerList.stream()
                    .filter(player -> !CorePlugin.getInstance().getPlayerManager().getPlayer(player).isVanished())
                    .filter(player -> !CorePlugin.getInstance().getPlayerManager().getPlayer(player).isStaffMode())
                    .collect(Collectors.toList());

            return split ? finalList.subList(0, 350) : finalList;
        }
    }
}
