package vip.potclub.core.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.player.PotPlayer;

import java.util.Objects;

public final class StaffUtil {

    private final static ServerType NETWORK = CorePlugin.getInstance().getServerManager().getNetwork();

    public static void sendAlert(Player player, String reason) {
        Bukkit.getOnlinePlayers().stream()
                .map(PotPlayer::getPlayer)
                .filter(Objects::nonNull)
                .filter(PotPlayer::isCanSeeStaffMessages)
                .forEach(potPlayer -> potPlayer.getPlayer().sendMessage(Color.translate("&7&o[" + player.getName() + ": " + NETWORK.getSecondaryColor() + reason + "&7&o]")));
    }
}
