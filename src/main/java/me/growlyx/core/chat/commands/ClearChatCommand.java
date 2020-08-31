package me.growlyx.core.chat.commands;

import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "clearchat", aliases = "cc", permission = "core.chat.clear"))
public class ClearChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String[] strings = new String[100];

            if (args.length == 0) {

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("core.staff")) {
                        player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + "&7You have bypassed &6ClearChat"));
                    } else {
                        player.sendMessage(strings);
                    }
                }

                String senderName;

                if (sender instanceof Player) {
                    senderName = sender.getName();
                } else {
                    senderName = ChatColor.DARK_RED + "Console";
                }

                Bukkit.broadcastMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("CHAT.CLEAR").replace("<user>", senderName)));


            }

            return false;

    }
}
