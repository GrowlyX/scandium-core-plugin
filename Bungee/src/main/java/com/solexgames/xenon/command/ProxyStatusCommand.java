package com.solexgames.xenon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.text.SimpleDateFormat;
import java.util.Date;

@CommandAlias("proxystatus|xenonstatus")
@CommandPermission("xenon.command.proxystatus")
public class ProxyStatusCommand extends BaseCommand {

    private static final long START_TIME = System.currentTimeMillis();

    @Default
    public void onCommand(ProxiedPlayer proxiedPlayer) {
        proxiedPlayer.sendMessage(new TextComponent(ChatColor.GOLD + ChatColor.BOLD.toString() + "Xenon Proxy Information:"));
        proxiedPlayer.sendMessage(new TextComponent(ChatColor.GRAY + "Proxy ID: " + ChatColor.WHITE + RedisBungee.getApi().getServerId()));
        proxiedPlayer.sendMessage(new TextComponent(ChatColor.AQUA + "  "));

        final SimpleDateFormat format = new SimpleDateFormat();
        final Date dateNow = new Date();
        final Date dateStart = new Date(START_TIME);

        proxiedPlayer.sendMessage(new TextComponent(ChatColor.GRAY + "Current time: " + ChatColor.WHITE + format.format(dateNow)));

        final long uptime = System.currentTimeMillis() - START_TIME;
        final long minutes = uptime / 60L / 1000L % 60L;
        final long hours = uptime / 60L / 60L / 1000L % 24L;
        final long days = uptime / 24L / 60L / 60L / 1000L;

        proxiedPlayer.sendMessage(new TextComponent(ChatColor.GRAY + "Startup time: " + ChatColor.WHITE + format.format(dateStart) + " (" + days + " days, " + hours + " hours, " + minutes + " minutes)"));
        proxiedPlayer.sendMessage(new TextComponent(ChatColor.GRAY + "Players: " + ChatColor.WHITE + ProxyServer.getInstance().getPlayers().size()));
        proxiedPlayer.sendMessage(new TextComponent(ChatColor.GRAY + "Players Global: " + ChatColor.WHITE + RedisBungee.getApi().getPlayerCount()));
        proxiedPlayer.sendMessage(new TextComponent(ChatColor.GRAY + "Max memory: " + ChatColor.WHITE + Runtime.getRuntime().maxMemory() / 1024L / 1024L + "mb"));
        proxiedPlayer.sendMessage(new TextComponent(ChatColor.GRAY + "Total memory: " + ChatColor.WHITE + Runtime.getRuntime().totalMemory() / 1024L / 1024L + "mb"));
        proxiedPlayer.sendMessage(new TextComponent(ChatColor.GRAY + "Free memory: " + ChatColor.WHITE + Runtime.getRuntime().freeMemory() / 1024L / 1024L + "mb"));
    }
}
