package com.solexgames.xenon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.solexgames.xenon.CorePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author GrowlyX
 * @since 6/24/2021
 */

@CommandPermission("xenon.command.timer")
@CommandAlias("timer|tmr|activetimer|motd")
public class TimerCommand extends BaseCommand {

    @HelpCommand
    public void doHelp(ProxiedPlayer proxiedPlayer, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("toggle")
    public void onToggle(ProxiedPlayer proxiedPlayer) {
        CorePlugin.getInstance().getXenonTopicTimer().setActive(!CorePlugin.getInstance().getXenonTopicTimer().isActive());

        proxiedPlayer.sendMessage(ChatColor.YELLOW + "You've set the value of \"" + ChatColor.GOLD + "active" + ChatColor.YELLOW + "\" to " + ChatColor.GOLD + CorePlugin.getInstance().getXenonTopicTimer().isActive());
    }

    @Subcommand("topic")
    public void onTopic(ProxiedPlayer proxiedPlayer, @Name("topic") String[] strings) {
        CorePlugin.getInstance().getXenonTopicTimer().setName(String.join(" ", strings));

        proxiedPlayer.sendMessage(ChatColor.YELLOW + "You've set the value of \"" + ChatColor.GOLD + "topic" + ChatColor.YELLOW + "\" to " + ChatColor.GOLD + CorePlugin.getInstance().getXenonTopicTimer().getName());
    }

    @Subcommand("date")
    public void onDate(ProxiedPlayer proxiedPlayer, @Name("date in long") long date) {
        CorePlugin.getInstance().getXenonTopicTimer().setEndsAt(new Date(date));

        proxiedPlayer.sendMessage(ChatColor.YELLOW + "You've set the value of \"" + ChatColor.GOLD + "endsAt" + ChatColor.YELLOW + "\" to " + ChatColor.GOLD + CorePlugin.getInstance().getXenonTopicTimer().getEndsAt().toString());
    }

    public static void main(String[] args) {
        final Calendar date = new GregorianCalendar();

        date.set(Calendar.MONTH, 5);
        date.set(Calendar.DAY_OF_MONTH, 30);
        date.set(Calendar.YEAR, 2021);
        date.set(Calendar.HOUR_OF_DAY, 14);
        date.set(Calendar.MINUTE, 0);

        System.out.println("long=" + date.getTime().getTime());
        System.out.println("formatted=" + new SimpleDateFormat("MM/dd/yyyy HH:mma").format(date.getTime()));
    }
}
