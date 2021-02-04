package vip.potclub.core.command.extend.essential;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.ranks.Rank;
import vip.potclub.core.util.Color;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommand extends BaseCommand {

    private final Comparator<Rank> RANK_COMPARATOR = Comparator.comparingInt(Rank::getWeight).reversed();
    private final Comparator<PotPlayer> PLAYER_DATA_COMPARATOR = Comparator.comparingInt(profile -> profile.getActiveGrant().getRank().getWeight());

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            List<Rank> ranks = Rank.getRanks().stream().sorted(RANK_COMPARATOR).collect(Collectors.toList());
            StringBuilder rankBuilder = new StringBuilder();
            StringBuilder playerBuilder = new StringBuilder();

            ranks.forEach(rankData -> {
                rankBuilder.append(Color.translate(rankData.getColor()));
                rankBuilder.append(rankData.getName());
                rankBuilder.append(Color.translate("&7, "));
            });

            Bukkit.getOnlinePlayers().stream().map(online ->
                    PotPlayer.getPlayer(online.getUniqueId()))
                    .filter(potPlayer -> !potPlayer.isVanished())
                    .sorted(PLAYER_DATA_COMPARATOR.reversed()).limit(100)
                    .forEach(playerData -> playerBuilder.append(Color.translate(
                            playerData.getActiveGrant().getRank().getColor() + playerData.getPlayer().getName() + "&f, ")
                    ));

            player.sendMessage(rankBuilder.toString());
            player.sendMessage(Color.translate("&f(" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ") " + playerBuilder.toString()));
        }
        return false;
    }
}
