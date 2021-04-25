package com.solexgames.core.command;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.database.Database;
import com.solexgames.core.manager.PlayerManager;
import com.solexgames.core.redis.RedisManager;
import com.solexgames.core.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author GrowlyX
 * @since 2021
 *
 * @see CommandExecutor
 * @see CorePlugin
 */

public abstract class BaseCommand extends Command implements PluginIdentifiableCommand {

    protected final String ONLY_PLAYERS = ChatColor.RED + "Only players can execute this command.";
    protected final String NO_PERMISSION = ChatColor.RED + "I'm sorry, but you do not have permission to perform this command.";

    protected BaseCommand() {
        super("");

        final String commandNameFromClazz = this.getClass().getSimpleName().replace("Command", "").toLowerCase();

        this.setLabel(commandNameFromClazz);
        this.setName(commandNameFromClazz);
        this.setAliases(this.getAliases());

        CorePlugin.getInstance().registerCommand(this);
    }

    public abstract boolean execute(CommandSender sender, String label, String[] args);

    public abstract List<String> getAliases();

    public String[] getHelpMessage(String... strings) {
        final List<String> list = new ArrayList<>();

        list.add(Color.MAIN_COLOR + "=== " + Color.SECONDARY_COLOR + "Showing help for " + Color.MAIN_COLOR + "/" + this.getLabel() + Color.SECONDARY_COLOR + ". " + Color.MAIN_COLOR + "===");
        list.addAll(Arrays.stream(strings).map(s -> Color.SECONDARY_COLOR + s.replace("<",  Color.MAIN_COLOR + "<")).collect(Collectors.toList()));
        list.add(Color.MAIN_COLOR + "== " + Color.SECONDARY_COLOR + "Showing a total of " + Color.MAIN_COLOR + strings.length + Color.SECONDARY_COLOR + " results. " + Color.MAIN_COLOR + "==");

        return list.toArray(new String[0]);
    }

    @Override
    public Plugin getPlugin() {
        return CorePlugin.getInstance();
    }

    public boolean isHidden() {
        return true;
    }
}
