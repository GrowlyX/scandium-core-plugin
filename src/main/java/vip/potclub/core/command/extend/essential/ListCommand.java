package vip.potclub.core.command.extend.essential;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.player.PotPlayerList;
import vip.potclub.core.rank.Rank;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        StringBuilder builder = new StringBuilder();

        Rank.getRegisteredRanks().stream().sorted(Comparator.comparingInt(Rank::getWeight).reversed()).forEach((rank) -> {
            builder.append(rank.getColor()).append(rank.getName().replaceAll("-", " ")).append(ChatColor.WHITE).append(", ");
        });

        builder.setCharAt(builder.length() - 2, '.');
        builder.append("\n");

        List<String> players = (new PotPlayerList(new ArrayList<>(Bukkit.getOnlinePlayers()))).visibleRankSorted().asColoredNames();

        builder.append(ChatColor.RESET).append("(").append(Bukkit.getOnlinePlayers().size()).append("/").append(CorePlugin.getInstance().getServer().getMaxPlayers()).append("): ").append(players.toString().replace("[", "").replace("]", ""));
        sender.sendMessage(builder.toString());
        return false;
    }
}
