package com.solexgames.command;

import com.solexgames.CorePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProxyStatusCommand extends Command {

    private static final long START_TIME = System.currentTimeMillis();

    public ProxyStatusCommand() {
        super("proxystatus", "xenon.command.status", "status");
    }

    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(new TextComponent(ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "Xenon Proxy Information:"));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "  "));

        SimpleDateFormat format = new SimpleDateFormat();

        Date dateNow = new Date();
        Date dateStart = new Date(START_TIME);

        sender.sendMessage(new TextComponent(ChatColor.AQUA + "Current time: " + ChatColor.DARK_AQUA + format.format(dateNow)));

        long uptime = System.currentTimeMillis() - START_TIME;
        long minutes = uptime / 60L / 1000L % 60L;
        long hours = uptime / 60L / 60L / 1000L % 24L;
        long days = uptime / 24L / 60L / 60L / 1000L;

        sender.sendMessage(new TextComponent(ChatColor.AQUA + "Startup time: " + ChatColor.DARK_AQUA + format.format(dateStart) + " (" + days + " days, " + hours + " hours, " + minutes + " minutes)"));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "Players: " + ProxyServer.getInstance().getPlayers().size()));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "Max memory: " + ChatColor.DARK_AQUA + Runtime.getRuntime().maxMemory() / 1024L / 1024L + "mb"));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "Total memory: " + ChatColor.DARK_AQUA + Runtime.getRuntime().totalMemory() / 1024L / 1024L + "mb"));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "Free memory: " + ChatColor.DARK_AQUA + Runtime.getRuntime().freeMemory() / 1024L / 1024L + "mb"));
    }
}
