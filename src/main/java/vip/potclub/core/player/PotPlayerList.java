package vip.potclub.core.player;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PotPlayerList {

    private final List<Player> players;

    public static PotPlayerList getVisiblyOnline(CommandSender sender) {
        return getOnline().visibleTo(sender);
    }

    public static PotPlayerList getOnline() {
        return new PotPlayerList(new ArrayList(CorePlugin.getInstance().getServer().getOnlinePlayers()));
    }

    public PotPlayerList visibleTo(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            this.players.removeIf((other) -> {
                return other != player && !player.canSee(other);
            });
        }

        return this;
    }

    public PotPlayerList canSee(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            this.players.removeIf((other) -> {
                return other == player || !other.canSee(player);
            });
        }

        return this;
    }

    public PotPlayerList visibleRankSorted() {
        this.players.sort(VISIBLE_RANK_ORDER);
        return this;
    }

    public final Comparator<Player> VISIBLE_RANK_ORDER = (a, b) -> {
        PotPlayer potPlayerA = PotPlayer.getPlayer(a.getUniqueId());
        PotPlayer potPlayerB = PotPlayer.getPlayer(b.getUniqueId());

        return -potPlayerA.getRank().compareTo(potPlayerB.getRank());
    };

    public List<String> asColoredNames() {
        return this.players.stream().map(OfflinePlayer::getUniqueId).map(PotPlayer::getPlayer).map(potPlayer -> potPlayer.getRank().getColor() + potPlayer.getPlayer().getDisplayName() + ChatColor.RESET).collect(Collectors.toList());
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    @ConstructorProperties({"players"})
    public PotPlayerList(List<Player> players) {
        this.players = players;
    }
}
