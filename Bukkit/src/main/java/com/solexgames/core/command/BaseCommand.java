package com.solexgames.core.command;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.database.Database;
import com.solexgames.core.manager.PlayerManager;
import com.solexgames.core.redis.RedisManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author GrowlyX
 * @since 2021
 *
 * @see CommandExecutor
 * @see CorePlugin
 */

public abstract class BaseCommand implements CommandExecutor {

    protected final String ONLY_PLAYERS = ChatColor.RED + "Only players can execute this command.";
    protected final String NO_PERMISSION = ChatColor.RED + "I'm sorry, but you do not have permission to perform this command.";

    protected PlayerManager playerManager;
    protected RedisManager client;
    protected Database database;

    /**
     * Created a new instance of BaseCommand.
     */
    protected BaseCommand() {
        this.playerManager = CorePlugin.getInstance().getPlayerManager();
        this.database = CorePlugin.getInstance().getCoreDatabase();
        this.client = CorePlugin.getInstance().getRedisManager();

        CorePlugin.getInstance().getCommand(this.getClass().getSimpleName().replace("Command", "").toLowerCase()).setExecutor(this);
    }

    protected abstract boolean execute(CommandSender sender, String label, String[] args);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return this.execute(sender, label, args);
    }
}
