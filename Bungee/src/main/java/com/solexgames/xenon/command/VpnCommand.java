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
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author GrowlyX
 * @since 7/2/2021
 */

@CommandAlias("vpn|api")
@CommandPermission("xenon.vpn.alerts")
public class VpnCommand extends BaseCommand {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("EEEE, dd MMMM yyyy @ h:mm a");

    static {
        FORMAT.setTimeZone(TimeZone.getTimeZone("EST"));
    }

    @Default
    public void onDefault(ProxiedPlayer proxiedPlayer) {
        proxiedPlayer.sendMessage(ChatColor.GREEN + "Calculating users who have connected with a VPN...");

        CompletableFuture.supplyAsync(() -> {
            final AtomicReference<Map<String, String>> reference = new AtomicReference<>();

            CorePlugin.getInstance().getJedisManager().get((jedis, throwable) -> {
                reference.set(jedis.hgetAll(CorePlugin.JEDIS_KEY_NETWORK_VPN_USERS));
            });

            return reference;
        }).whenComplete((mapAtomicReference, throwable) -> {
            if (mapAtomicReference == null || mapAtomicReference.get().isEmpty()) {
                proxiedPlayer.sendMessage(ChatColor.RED + "Error: No one has tried to connect with a VPN.");
                return;
            }

            final int blockedLogins = mapAtomicReference.get().size();

            proxiedPlayer.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Blocked VPN Logins:");
            proxiedPlayer.sendMessage(ChatColor.WHITE.toString() + blockedLogins + ChatColor.GRAY + " login" + (blockedLogins == 1 ? " was" : "s were") + " fetched and blocked.");
            proxiedPlayer.sendMessage(" ");

            mapAtomicReference.get().forEach((username, date) -> {
                final long parsed = Long.parseLong(date);
                final String formattedDate = VpnCommand.FORMAT.format(new Date(parsed));

                proxiedPlayer.sendMessage(ChatColor.GRAY + " - " + ChatColor.YELLOW + username + ChatColor.GRAY + " (" + formattedDate + ")");
            });
        });
    }

    @Subcommand("resume|pause")
    public void onToggle(ProxiedPlayer proxiedPlayer) {
        PlayerListener.VPN_CHECKS = !PlayerListener.VPN_CHECKS;

        proxiedPlayer.sendMessage(ChatColor.YELLOW + "API operations have been " + (PlayerListener.VPN_CHECKS ? "resumed" : "paused"));
    }
}
