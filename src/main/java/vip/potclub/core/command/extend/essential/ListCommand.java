package vip.potclub.core.command.extend.essential;

import com.solexgames.perms.rank.Rank;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.list.PlayerList;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {

        StringBuilder builder = new StringBuilder();
        Rank[] ranks = Arrays.copyOf(Rank.getRanks().toArray(new Rank[0]), Rank.getRanks().size());
        ArrayUtils.reverse(ranks);
        Arrays.stream(ranks).forEach(rank
                -> builder.append(Color.translate(rank.getData().getColorPrefix())).append(rank.getData().getName()).append(ChatColor.WHITE).append(", "));

        builder.setCharAt(builder.length() - 2, '.');
        builder.append("\n");

        List<String> players = new PlayerList(PotPlayer.profilePlayers
                .keySet().stream().filter(uuid -> Bukkit.getPlayer(uuid) != null)
                .map(Bukkit::getPlayer).collect(Collectors.toList()))
                .visibleRankSorted().asColoredNames();

        builder.append(ChatColor.RESET).append("(").append(Bukkit.getOnlinePlayers().size()).append("/")
                .append(CorePlugin.getInstance().getServer().getMaxPlayers()).append("): ")
                .append(players.toString().replace("[", "").replace("]", ""));

        sender.sendMessage(builder.toString());
        return false;
    }
}
