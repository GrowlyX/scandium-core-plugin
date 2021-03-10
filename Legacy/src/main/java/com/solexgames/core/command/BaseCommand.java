package com.solexgames.core.command;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.database.Database;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.manager.PlayerManager;
import com.solexgames.core.redis.RedisManager;
import com.solexgames.core.util.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class BaseCommand implements CommandExecutor {

    public PlayerManager playerManager;
    public RedisManager client;
    public Database database;

    public BaseCommand() {
        this.playerManager = CorePlugin.getInstance().getPlayerManager();
        this.database = CorePlugin.getInstance().getCoreDatabase();
        this.client = CorePlugin.getInstance().getRedisManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(Color.translate("&cThis command was not created properly."));
        return false;
    }
}
