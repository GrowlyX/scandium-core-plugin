package vip.potclub.core.command;

import org.bukkit.command.CommandExecutor;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.database.Database;
import vip.potclub.core.manager.PlayerManager;
import vip.potclub.core.redis.RedisClient;

public abstract class BaseCommand implements CommandExecutor {

    public PlayerManager playerManager;
    public RedisClient client;
    public Database database;

    public BaseCommand() {
        this.playerManager = CorePlugin.getInstance().getPlayerManager();
        this.database = CorePlugin.getInstance().getCoreMongoDatabase();
        this.client = CorePlugin.getInstance().getRedisClient();
    }
}
