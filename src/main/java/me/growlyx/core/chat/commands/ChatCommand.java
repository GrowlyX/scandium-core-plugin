package me.growlyx.core.chat.commands;

import me.growlyx.core.Core;
import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

import static me.growlyx.core.chat.ChatListener.chatEnabled;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "chat", permission = "core.chat"))
public class ChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] string) {

        String[] args = string;


        if (args.length == 0) {

            Player player = (Player) sender;

            player.sendMessage(CC.translate("&7&m--------------------------------------"));
            player.sendMessage(CC.translate("&6&lChat Manager"));
            player.sendMessage(CC.translate("&7&m--------------------------------------"));
            player.sendMessage(CC.translate("&6/chat mute &7- &fMute the Chat"));
            player.sendMessage(CC.translate("&6/chat clear &7- &fClear the Chat"));
            player.sendMessage(CC.translate("&6/chat delay/slow &7- &fSlow the Chat"));
            player.sendMessage(CC.translate("&7&m--------------------------------------"));

            return true;

        } else if (args[0].equals("clear") && sender.hasPermission("core.chat.clear")) {

            String[] strings = new String[120];

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("core.staff")) {
                    player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.CHAT.BYPASS").replace("<bypass>", "Clear Chat")));
                } else {
                    player.sendMessage(strings);
                }
            }

            String senderName2;

            if (sender instanceof Player) {
                senderName2 = sender.getName();
            } else {
                senderName2 = ChatColor.DARK_RED + "Console";
            }

            Bukkit.broadcastMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.CHAT.CLEAR").replace("<user>", senderName2)));

        } else if (args[0].equals("mute") && sender.hasPermission("core.chat.mute")) {

            Player player = (Player) sender;

            if (Messages.aboolean("BOOLEAN.CHAT-MUTED")) {

                Core.instance.m.getConfig().set("BOOLEAN.CHAT-MUTED", false);

                String senderName;

                if (sender instanceof Player) {
                    senderName = sender.getName();
                } else {
                    senderName = ChatColor.DARK_RED + "Console";
                }

                Bukkit.broadcastMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.CHAT.MUTE").replace("<user>", senderName)));

                return true;

            } else if (!Messages.aboolean("BOOLEAN.CHAT-MUTED")) {

                Core.instance.m.getConfig().set("BOOLEAN.CHAT-MUTED", true);

                String senderName;

                if (sender instanceof Player) {
                    senderName = sender.getName();
                } else {
                    senderName = ChatColor.DARK_RED + "Console";
                }

                Bukkit.broadcastMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.CHAT.MUTE").replace("<user>", senderName)));

                return true;
            }


        }

        return false;
    }
}