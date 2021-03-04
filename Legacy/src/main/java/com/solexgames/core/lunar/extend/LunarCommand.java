package com.solexgames.core.lunar.extend;

import com.solexgames.core.lunar.AbstractClientInjector;
import com.solexgames.core.util.Color;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LunarCommand extends AbstractClientInjector {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }
/*
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (args.length == 0) player.sendMessage(Color.translate("&cUsage: /" + s + " <list|player>."));
        if (args.length == 1) {
            if ("list".equals(args[0])) {
                player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                player.sendMessage(Color.translate("&b&lLunar Client Players:"));
                player.sendMessage("  ");
                this.lunarClient.getPlayersRunningLunarClient().forEach(player1 -> player.sendMessage(Color.translate(" &7* &a" + player1.getName())));
                player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
            } else {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target != null) {
                    boolean isLunar = this.lunarClient.isRunningLunarClient(player);
                    player.sendMessage(Color.translate((isLunar ? "&a" : "&c") + target.getName() + " is " + (isLunar ? "" : "NOT ") + "using Lunar Client."));
                } else player.sendMessage(Color.translate("&cUsage: /" + s + " <list|player>."));
            }
        }
        return false;
    }

    public void enableModModules(Player player) {
        LunarClientAPI.getInstance().giveAllStaffModules(player);
    }

    public void disableModModules(Player player) {
        LunarClientAPI.getInstance().disableAllStaffModules(player);
    }*/
}
