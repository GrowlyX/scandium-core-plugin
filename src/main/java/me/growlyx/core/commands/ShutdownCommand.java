package me.growlyx.core.commands;

import me.growlyx.core.Core;
import me.growlyx.core.tasks.ShutdownTask;
import me.growlyx.core.utils.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "shutdown", permission = "core.admin.shutdown"))
public class ShutdownCommand implements CommandExecutor {
    private ShutdownTask shutdownTask;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (args.length != 1) {

            sender.sendMessage(CC.translate("&7&m--------------------------------------"));
            sender.sendMessage(CC.translate("&6&lShutdown Manager"));
            sender.sendMessage(CC.translate("&7&m--------------------------------------"));
            sender.sendMessage(CC.translate("&6/shutdown &7- &fDisplays Help Message."));
            sender.sendMessage(CC.translate("&6/shutdown start&7- &fStart Shutdown."));
            sender.sendMessage(CC.translate("&6/shutdown cancel&7- &fCancel Shutdown."));
            sender.sendMessage(CC.translate("&6/shutdown time&7- &fTime until Shutdown."));
            sender.sendMessage(CC.translate("&7&m--------------------------------------"));
            return true;
        }
        if (args[0].equalsIgnoreCase("time")) {

            if (this.shutdownTask == null) {

                sender.sendMessage(CC.translate("&cThe server is not shutting down."));

            } else {

                sender.sendMessage(CC.translate("&7[&4&l!&7] &7The server will shutdown in &4" + this.shutdownTask.getSecondsUntilShutdown() + "&7 seconds!"));
            }

            return true;

        }
        if (args[0].equalsIgnoreCase("cancel")) {

            if (this.shutdownTask == null) {
                sender.sendMessage(CC.translate("&cError: The server is not scheduled to shut down."));
            }
            else {
                this.shutdownTask.cancel();
                this.shutdownTask = null;
                sender.sendMessage(CC.translate("&aThe server shutdown has been canceled."));
            }

        } if (args[0].equalsIgnoreCase("start")) {

            int seconds;

            try {

                seconds = Integer.parseInt(args[1]);

            } catch (NumberFormatException e) {

                sender.sendMessage(CC.translate("&cError: You must input a valid number!"));
                return true;

            } if (seconds <= 0) {

                sender.sendMessage(CC.translate("&cError: You must input a number greater than 0!"));
                return true;

            } if (this.shutdownTask == null) {

                (this.shutdownTask = new ShutdownTask(Core.getPlugin(Core.class), seconds)).runTaskTimer((Plugin) Core.getPlugin(Core.class), 20L, 20L);

            } else {
                this.shutdownTask.setSecondsUntilShutdown(seconds);
            }

            sender.sendMessage(CC.translate("&7[&4&l!&7] &7The server will shutdown in &4" + seconds + "&7 seconds!"));

        }


        return true;
    }
}
