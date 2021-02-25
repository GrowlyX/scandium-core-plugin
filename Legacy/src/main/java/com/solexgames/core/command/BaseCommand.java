package com.solexgames.core.command;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.database.Database;
import com.solexgames.core.manager.PlayerManager;
import com.solexgames.core.redis.RedisClient;
import org.bukkit.command.CommandExecutor;

public abstract class BaseCommand implements CommandExecutor {

    public PlayerManager playerManager;
    public RedisClient client;
    public Database database;

    public BaseCommand() {
        this.playerManager = CorePlugin.getInstance().getPlayerManager();
        this.database = CorePlugin.getInstance().getCoreDatabase();
        this.client = CorePlugin.getInstance().getRedisClient();
    }
}