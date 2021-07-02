package com.solexgames.xenon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.solexgames.xenon.CorePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author GrowlyX
 * @since 7/2/2021
 */

@CommandAlias("vpn")
@CommandPermission("xenon.vpn.alerts")
public class VpnCommand extends BaseCommand {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("EEEE, dd MMMM yyyy @ h:mm a");

    @Default
    public void onDefault(ProxiedPlayer proxiedPlayer) {
        if (CorePlugin.getInstance().getVpnUsers().isEmpty()) {
            proxiedPlayer.sendMessage(ChatColor.RED + "No one has tried to connect with a VPN.");
            return;
        }

        proxiedPlayer.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Blocked VPN Logins:");
        CorePlugin.getInstance().getVpnUsers().forEach((s, aLong) -> {
            proxiedPlayer.sendMessage(ChatColor.GRAY + " - " + ChatColor.YELLOW + s + ChatColor.GRAY + " (" + VpnCommand.FORMAT.format(new Date(aLong)) + ")");
        });
    }
}
