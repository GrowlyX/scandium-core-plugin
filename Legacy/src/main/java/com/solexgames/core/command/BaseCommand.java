package com.solexgames.core.command;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.database.Database;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.manager.PlayerManager;
import com.solexgames.core.redis.RedisManager;
import com.solexgames.core.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class BaseCommand implements CommandExecutor {

    protected final String ONLY_PLAYERS = ChatColor.RED + "Only players can execute this command.";
    protected final String NO_PERMISSION = ChatColor.RED + "I'm sorry, but you do not have permission to perform this command.";

    protected PlayerManager playerManager;
    protected RedisManager client;
    protected Database database;

    protected BaseCommand() {
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
