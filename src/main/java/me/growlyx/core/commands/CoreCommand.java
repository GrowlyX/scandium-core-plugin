package me.growlyx.core.commands;

import me.growlyx.core.Core;
import me.growlyx.core.utils.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "core", aliases = "servercore"))
public class CoreCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (args.length == 0) {

                player.sendMessage(CC.translate("&7&m--------------------------------------"));
                player.sendMessage(CC.translate("&6&lCore &7- &fv1.0"));
                player.sendMessage(CC.translate("&7&m--------------------------------------"));
                player.sendMessage(CC.translate("&7Use &6/core help &7for more information."));
                player.sendMessage(CC.translate("&7&m--------------------------------------"));

            } else if (args[0].equals("help")) {

                player.sendMessage(CC.translate("&7&m--------------------------------------"));
                player.sendMessage(CC.translate("&6&lCore &7- &fHelp"));
                player.sendMessage(CC.translate("&7&m--------------------------------------"));
                player.sendMessage(CC.translate("&6/core &7- &fCore Command"));
                player.sendMessage(CC.translate("&6/player &7- &fPlayer Command Info"));
                player.sendMessage(CC.translate("&6/serverinfo &7- &fServer Information"));
                player.sendMessage(CC.translate("&6/clearchat &7- &fClear Server Chat"));
                player.sendMessage(CC.translate("&7&m--------------------------------------"));

            } else if (args[0].equals("reload")) {

                Core.instance.reloadConfig();
                Core.instance.d.reloadConfig();
                Core.instance.t.reloadConfig();
                Core.instance.l.reloadConfig();
                Core.instance.m.reloadConfig();

                player.sendMessage(CC.translate("&7&m--------------------------------------"));
                player.sendMessage(CC.translate("&6&lCore &7- &fReloaded!"));
                player.sendMessage(CC.translate("&7&m--------------------------------------"));

            } else if (args[0].equals("moose")) {

                player.sendMessage(CC.translate("&7&m--------------------------------------"));
                player.sendMessage(CC.translate("&6&lCore &7- &fMOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOSE!"));
                player.sendMessage(CC.translate("&7&m--------------------------------------"));

            } else {

                System.out.println("Only players can execute this command.");

            }

            return false;
        }

        return false;

    }
}
