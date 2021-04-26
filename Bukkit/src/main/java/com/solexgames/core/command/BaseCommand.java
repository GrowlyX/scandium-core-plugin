package com.solexgames.core.command;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.command.CommandHelpBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

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

    public void getHelpMessage(int page, CommandSender sender, String... strings) {
        final CommandHelpBuilder helpBuilder = new CommandHelpBuilder(10, this.getLabel());

        helpBuilder.display(sender, page, Arrays.asList(strings));
    }

    @Override
    public Plugin getPlugin() {
        return CorePlugin.getInstance();
    }

    public boolean isHidden() {
        return true;
    }
}
