package com.solexgames.core.command.extend.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.StaffUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpAllCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;

        if (player.hasPermission("scandium.command.tpall")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        Bukkit.getOnlinePlayers().forEach(player1 -> player1.teleport(player.getLocation()));
        StaffUtil.sendAlert(player, "teleported all players");

        return false;
    }
}
