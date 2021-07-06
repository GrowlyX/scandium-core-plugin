package com.solexgames.xenon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.solexgames.xenon.CorePlugin;
import com.solexgames.xenon.listener.PlayerListener;
import com.solexgames.xenon.redis.annotation.Subscription;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author GrowlyX
 * @since 7/2/2021
 */

@CommandAlias("vpn|api")
@CommandPermission("xenon.vpn.alerts")
public class VpnCommand extends BaseCommand {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("EEEE, dd MMMM yyyy @ h:mm a");

    @Default
    public void onDefault(ProxiedPlayer proxiedPlayer) {
        if (CorePlugin.getInstance().getVpnUsers().isEmpty()) {
            proxiedPlayer.sendMessage(ChatColor.RED + "Error: No one has tried to connect with a VPN.");
            return;
        }

        proxiedPlayer.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Blocked VPN Logins:");
        CorePlugin.getInstance().getVpnUsers().forEach((s, aLong) -> {
            proxiedPlayer.sendMessage(ChatColor.GRAY + " - " + ChatColor.YELLOW + s + ChatColor.GRAY + " (" + VpnCommand.FORMAT.format(new Date(aLong)) + ")");
        });
    }

    @Subcommand("resume|pause")
    public void onToggle(ProxiedPlayer proxiedPlayer) {
        PlayerListener.VPN_CHECKS = !PlayerListener.VPN_CHECKS;

        proxiedPlayer.sendMessage(ChatColor.YELLOW + "API operations have been " + (PlayerListener.VPN_CHECKS ? "resumed" : "paused"));
    }

    static {
        FORMAT.setTimeZone(TimeZone.getTimeZone("EST"));
    }
}
