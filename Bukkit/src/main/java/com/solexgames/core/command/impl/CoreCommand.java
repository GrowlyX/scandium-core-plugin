package com.solexgames.core.command.impl;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.impl.ScandiumMenu;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

public class CoreCommand extends BukkitCommand {

    public CoreCommand(String name) {
        super(name, "Base Command for " + name + " Core.", "Usage: /" + name + " <debug|disallow|panel>", Collections.singletonList("core"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
            return false;
        }

        final Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "I'm sorry, but you do not have permission to perform this command.");
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <debug|disallow|panel|threads>.");
        }
        if (args.length > 0) {
            switch (args[0]) {
                case "threads":
                    Thread.getAllStackTraces().keySet().stream()
                            .filter(thread -> thread.isAlive() && thread.isDaemon())
                            .forEachOrdered(thread -> player.sendMessage(Color.MAIN_COLOR + thread.getName() + Color.SECONDARY_COLOR + " (ID: " + Color.MAIN_COLOR + thread.getId() + Color.SECONDARY_COLOR + ") (State: " + Color.MAIN_COLOR + thread.getState().name() + Color.SECONDARY_COLOR + ")"));
                    break;
                case "debug":
                    player.sendMessage(new String[] {
                            "  ",
                            StringUtil.getCentered(Color.MAIN_COLOR + ChatColor.BOLD.toString() + CorePlugin.getInstance().getConfig().getString("core-settings.name") + " Server Instance:"),
                            "",
                            StringUtil.getCentered(Color.SECONDARY_COLOR + "Current Threads: " + Color.MAIN_COLOR + Thread.getAllStackTraces().keySet().stream().filter(thread -> thread.isAlive() && thread.isDaemon()).count()),
                            StringUtil.getCentered(Color.SECONDARY_COLOR + "Jedis Active: " + Color.MAIN_COLOR + CorePlugin.getInstance().getRedisManager().getJedisPool().getNumActive()),
                            StringUtil.getCentered(Color.SECONDARY_COLOR + "Jedis Idle: " + Color.MAIN_COLOR + CorePlugin.getInstance().getRedisManager().getJedisPool().getNumIdle()),
                            StringUtil.getCentered(Color.SECONDARY_COLOR + "Jedis Waiters: " + Color.MAIN_COLOR + CorePlugin.getInstance().getRedisManager().getJedisPool().getNumWaiters()),
                            StringUtil.getCentered(Color.SECONDARY_COLOR + "Current TPS: " + Color.MAIN_COLOR + String.format("%.2f", Math.min(CorePlugin.getInstance().getTpsRunnable().getTPS(), 20.0))),
                            "",
                            StringUtil.getCentered(ChatColor.GRAY + ChatColor.ITALIC.toString() + "For more information on threads, use /" + label + " threads."),
                            "  "
                    });
                    break;
                case "disallow":
                    player.sendMessage(Color.translate((CorePlugin.getInstance().isDisallow() ? Color.MAIN_COLOR + "[" + CorePlugin.getInstance().getConfig().getString("core-settings.name") + "] &cDisabled disallow." : Color.MAIN_COLOR + "[" + CorePlugin.getInstance().getConfig().getString("core-settings.name") + "] &aEnabled disallow.")));
                    CorePlugin.getInstance().setDisallow(!CorePlugin.getInstance().isDisallow());
                    break;
                case "panel":
                    new ScandiumMenu(player).open(player);
                    break;
                default:
                    sender.sendMessage(Color.SECONDARY_COLOR + "Usage: /" + Color.MAIN_COLOR + label + ChatColor.WHITE + " <debug|disallow|panel>.");
                    break;
            }
        }
        return false;
    }
}
