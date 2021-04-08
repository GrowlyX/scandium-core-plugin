package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.manager.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.setspawn")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        ServerManager manager = CorePlugin.getInstance().getServerManager();
        manager.setSpawnLocation(player.getLocation());

        CorePlugin.getInstance().getConfig().set("locations.spawn", manager.getSpawnLocation());
        CorePlugin.getInstance().saveConfig();

        player.sendMessage(ChatColor.GREEN + "The spawn location has been set to your current location!");

        return false;
    }
}