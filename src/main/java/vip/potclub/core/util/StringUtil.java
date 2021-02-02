package vip.potclub.core.util;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import vip.potclub.core.player.PotPlayer;

import java.util.Arrays;

public final class StringUtil {

    public static String buildMessage(String[] args, int start) {
        return start >= args.length ? "" : ChatColor.stripColor(String.join(" ", Arrays.copyOfRange(args, start, args.length)));
    }

    public static void sendPrivateMessage(Player sender, Player target, String message) {
        String toMessage = ChatColor.GRAY + "(To " + target.getDisplayName() + ChatColor.GRAY + ") " + message;
        String fromMessage = ChatColor.GRAY + "(From " + sender.getDisplayName() + ChatColor.GRAY + ") " + message;

        sender.sendMessage(toMessage);
        target.sendMessage(fromMessage);

        PotPlayer potPlayer = PotPlayer.getPlayer(target);
        if (potPlayer.isCanReceiveDmsSounds()) {
            target.playSound(target.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
        }
    }
}
