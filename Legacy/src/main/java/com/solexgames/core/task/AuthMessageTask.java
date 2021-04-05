package com.solexgames.core.task;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

/**
 * @author GrowlyX
 * @since March 2021
 */

public class AuthMessageTask extends BukkitRunnable {

    private final String[] verifyMessage;
    private final String[] setupMessage;

    public AuthMessageTask() {
        this.verifyMessage = new String[] {
                "",
                Color.SECONDARY_COLOR + "Please use " + Color.MAIN_COLOR + "/2fa" + Color.SECONDARY_COLOR + " to confirm your 2FA.",
                Color.SECONDARY_COLOR + "Get the code from your 2FA app that you used to setup 2FA when you first joined.",
                "",
        };
        this.setupMessage = new String[] {
                "",
                Color.SECONDARY_COLOR + "Please type " + Color.MAIN_COLOR + "/2fa" + Color.SECONDARY_COLOR + " to setup your 2FA!",
                Color.SECONDARY_COLOR + "You'll receive a map with a QR Code that needs to be scanned.",
                "",
                Color.SECONDARY_COLOR + "Use an app such as " + ChatColor.RED + "Authy" + Color.SECONDARY_COLOR + " or " + ChatColor.DARK_AQUA + "Google Auth" + Color.SECONDARY_COLOR + " to scan and receive your code!",
                ""
        };

        this.runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 5L * 20L);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().stream()
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(Objects::nonNull)
                .filter(potPlayer -> potPlayer.getPlayer().hasPermission("scandium.2fa"))
                .forEach(this::sendAuthMessage);
    }

    public void sendAuthMessage(PotPlayer potPlayer) {
        if (potPlayer.isSetupSecurity() && !potPlayer.isVerify()) {
            potPlayer.getPlayer().sendMessage(this.setupMessage);
        } else if (potPlayer.isVerify()) {
            potPlayer.getPlayer().sendMessage(this.verifyMessage);
        }
    }
}
