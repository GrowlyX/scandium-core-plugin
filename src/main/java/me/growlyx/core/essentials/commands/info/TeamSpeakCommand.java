package me.growlyx.core.essentials.commands.info;

import me.growlyx.core.Core;
import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "teamspeak", aliases = {"ts", "ts3"}))
public class TeamSpeakCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {

            if (args.length == 0) {

                if (Messages.aboolean("MESSAGES.INFO.TEAMSPEAK.ENABLED")) {

                    for (String string: Core.instance.m.getConfig().getStringList("MESSAGES.INFO.TEAMSPEAK.MESSAGE")) {
                        sender.sendMessage(CC.translate(string));
                    }

                }

            } else {

                System.out.println("Only players can execute this command.");

            }

            return false;
        }

        return false;
    }
}
