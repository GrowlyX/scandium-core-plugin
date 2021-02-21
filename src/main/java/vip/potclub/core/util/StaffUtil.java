package vip.potclub.core.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.PotPlayer;

import java.util.Objects;

public final class StaffUtil {

    public static void sendAlert(Player player, String reason) {
        if (CorePlugin.STAFF_ALERTS_COMMAND) {
            Bukkit.getOnlinePlayers().stream()
                    .map(PotPlayer::getPlayer)
                    .filter(Objects::nonNull)
                    .filter(PotPlayer::isCanSeeStaffMessages)
                    .filter(potPlayer -> potPlayer.getPlayer().hasPermission(CorePlugin.getInstance().getConfig().getString("settings.staff-command-alerts-permission")))
                    .forEach(potPlayer -> potPlayer.getPlayer().sendMessage(Color.translate(CorePlugin.getInstance().getConfig().getString("settings.staff-command-alerts-format").replace("<playername>", player.getName()).replace("<message>", reason))));
        }
    }
}
