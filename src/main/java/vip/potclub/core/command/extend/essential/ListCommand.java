package vip.potclub.core.command.extend.essential;

import com.solexgames.perms.profile.Profile;
import com.solexgames.perms.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;
import vip.potclub.util.external.chat.ChatComponentBuilder;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommand extends BaseCommand {

    private final Comparator<Rank> RANK_COMPARATOR = Comparator.comparingInt(Rank::getWeight).reversed();
    private final Comparator<Profile> PLAYER_DATA_COMPARATOR = Comparator.comparingInt(profile -> profile.getActiveGrant().getRank().getData().getWeight());

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            List<Rank> ranks = Rank.getRanks().stream().sorted(RANK_COMPARATOR).collect(Collectors.toList());
            StringBuilder rankBuilder = new StringBuilder();
            StringBuilder playerBuilder = new StringBuilder();

            ranks.forEach(rankData -> {
                rankBuilder.append(Color.translate(rankData.getData().getColorPrefix()));
                rankBuilder.append(rankData.getData().getName());
                rankBuilder.append(Color.translate("&f, "));
            });

            Bukkit.getOnlinePlayers().stream().map(online ->
                    Profile.getByUuid(online.getUniqueId()))
                    .filter(profile -> !PotPlayer.getPlayer(profile.getUuid()).isVanished())
                    .sorted(PLAYER_DATA_COMPARATOR.reversed()).limit(100)
                    .forEach(playerData -> playerBuilder.append(Color.translate(
                            playerData.getActiveGrant().getRank().getData().getColorPrefix() + playerData.getPlayer().getName() + "&f, ")
                    ));

            player.sendMessage(rankBuilder.toString());
            player.sendMessage(Color.translate("&f(" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ") " + playerBuilder.toString()));
        }
        return false;
    }
}
