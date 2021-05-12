package com.solexgames.xenon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
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
        proxiedPlayer.sendMessage(new TextComponent(ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "Xenon Proxy Information:"));
        proxiedPlayer.sendMessage(new TextComponent(ChatColor.AQUA + "  "));

        final SimpleDateFormat format = new SimpleDateFormat();
        final Date dateNow = new Date();
        final Date dateStart = new Date(START_TIME);

        proxiedPlayer.sendMessage(new TextComponent(ChatColor.AQUA + "Current time: " + ChatColor.DARK_AQUA + format.format(dateNow)));

        final long uptime = System.currentTimeMillis() - START_TIME;
        final long minutes = uptime / 60L / 1000L % 60L;
        final long hours = uptime / 60L / 60L / 1000L % 24L;
        final long days = uptime / 24L / 60L / 60L / 1000L;

        proxiedPlayer.sendMessage(new TextComponent(ChatColor.AQUA + "Startup time: " + ChatColor.DARK_AQUA + format.format(dateStart) + " (" + days + " days, " + hours + " hours, " + minutes + " minutes)"));
        proxiedPlayer.sendMessage(new TextComponent(ChatColor.AQUA + "Players: " + ProxyServer.getInstance().getPlayers().size()));
        proxiedPlayer.sendMessage(new TextComponent(ChatColor.AQUA + "Max memory: " + ChatColor.DARK_AQUA + Runtime.getRuntime().maxMemory() / 1024L / 1024L + "mb"));
        proxiedPlayer.sendMessage(new TextComponent(ChatColor.AQUA + "Total memory: " + ChatColor.DARK_AQUA + Runtime.getRuntime().totalMemory() / 1024L / 1024L + "mb"));
        proxiedPlayer.sendMessage(new TextComponent(ChatColor.AQUA + "Free memory: " + ChatColor.DARK_AQUA + Runtime.getRuntime().freeMemory() / 1024L / 1024L + "mb"));
    }
}
