package com.solexgames.core.command.impl;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.menu.impl.ScandiumMenu;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(label = "core", aliases = {"scandium"})
public class CoreCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            this.getHelpMessage(1, sender,
                    "/" + label + "debug",
                    "/" + label + "panel",
                    "/" + label + "threads"
            );
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
                            StringUtil.getCentered(Color.SECONDARY_COLOR + "Jedis Active: " + Color.MAIN_COLOR + CorePlugin.getInstance().getJedisManager().getJedisPool().getNumActive()),
                            StringUtil.getCentered(Color.SECONDARY_COLOR + "Jedis Idle: " + Color.MAIN_COLOR + CorePlugin.getInstance().getJedisManager().getJedisPool().getNumIdle()),
                            StringUtil.getCentered(Color.SECONDARY_COLOR + "Jedis Waiters: " + Color.MAIN_COLOR + CorePlugin.getInstance().getJedisManager().getJedisPool().getNumWaiters()),
                            StringUtil.getCentered(Color.SECONDARY_COLOR + "Current TPS: " + Color.MAIN_COLOR + String.format("%.2f", Math.min(CorePlugin.getInstance().getTpsRunnable().getTPS(), 20.0))),
                            "",
                            StringUtil.getCentered(ChatColor.GRAY + ChatColor.ITALIC.toString() + "For more information on threads, use /" + label + " threads."),
                            "  "
                    });
                    break;
                case "panel":
                    new ScandiumMenu(player).open(player);
                    break;
                default:
                    this.getHelpMessage(1, sender,
                            "/" + label + "debug",
                            "/" + label + "panel",
                            "/" + label + "threads"
                    );
                    break;
            }
        }

        return false;
    }
}
