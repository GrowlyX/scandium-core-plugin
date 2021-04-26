package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.manager.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetSpawnCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.setspawn")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        final ServerManager manager = CorePlugin.getInstance().getServerManager();
        final String location = CorePlugin.GSON.toJson(player.getLocation());

        manager.setSpawnLocation(player.getLocation());

        CorePlugin.getInstance().getConfig().set("locations.spawnpoint", location);
        CorePlugin.getInstance().saveConfig();

        player.sendMessage(ChatColor.GREEN + "The spawn location has been set to your current location!");

        return false;
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }
}
