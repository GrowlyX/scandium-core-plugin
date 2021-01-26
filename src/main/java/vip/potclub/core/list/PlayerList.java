package vip.potclub.core.list;

import com.solexgames.perms.profile.Profile;
import com.solexgames.perms.util.PlayerUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.PotPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class PlayerList {

    public static final Comparator<Player> VISIBLE_RANK_ORDER = (a, b) -> {
        PotPlayer potPlayer = PotPlayer.getPlayer(a.getUniqueId());
        PotPlayer potPlayerTwo = PotPlayer.getPlayer(b.getUniqueId());
        return -potPlayer.getRankName().compareTo(potPlayerTwo.getRankName());
    };

    private final List<Player> players;

    public static PlayerList getVisiblyOnline(CommandSender sender) {
        return getOnline().visibleTo(sender);
    }

    public static PlayerList getOnline() {
        return new PlayerList(new ArrayList<>(CorePlugin.getInstance().getServer().getOnlinePlayers()));
    }

    public PlayerList visibleTo(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            this.players.removeIf(other -> other != player && !player.canSee(other));
        }
        return this;
    }

    public PlayerList canSee(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            this.players.removeIf(other -> other == player || !other.canSee(player));
        }
        return this;
    }

    public PlayerList visibleRankSorted() {
        this.players.sort(VISIBLE_RANK_ORDER);
        return this;
    }

    public List<String> asColoredNames() {
        return this.players.stream()
                .map(Player::getUniqueId)
                .map(PotPlayer::getPlayer)
                .map(potPlayer -> Profile.getByUuid(potPlayer.getUuid()).getActiveGrant().getRank().getData().getColorPrefix() + potPlayer.getName() + ChatColor.RESET)
                .collect(Collectors.toList());
    }
}
