package vip.potclub.core.command;

import org.bukkit.command.CommandExecutor;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.database.Database;
import vip.potclub.core.manager.PlayerManager;

public abstract class BaseCommand implements CommandExecutor {

    public PlayerManager playerManager;
    public Database database;

    public BaseCommand() {
        this.playerManager = CorePlugin.getInstance().getPlayerManager();
        this.database = CorePlugin.getInstance().getDatabase();
    }
}
