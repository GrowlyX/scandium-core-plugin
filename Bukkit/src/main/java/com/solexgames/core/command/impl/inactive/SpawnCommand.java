package com.solexgames.core.command.impl.inactive;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.manager.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command(label = "spawn", hidden = false)
public class SpawnCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.spawn")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        final ServerManager manager = CorePlugin.getInstance().getServerManager();

        if (manager.getSpawnLocation() != null) {
            player.teleport(manager.getSpawnLocation());
            player.sendMessage(ChatColor.GREEN + "You've been teleported to spawn!");
        } else {
            player.sendMessage(ChatColor.RED + "The spawn location hasn't been set on this server!");
        }

        return false;
    }
}
