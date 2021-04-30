package com.solexgames.core.command;

import com.google.common.collect.Sets;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.command.CommandHelpBuilder;
import com.solexgames.core.util.command.CustomHelpTopic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
        Bukkit.getHelpMap().addTopic(new CustomHelpTopic(this, Sets.newHashSet(this.getAliases())));
    }

    public abstract boolean execute(CommandSender sender, String label, String[] args);

    public abstract List<String> getAliases();

    public void getHelpMessage(int page, CommandSender sender, String... strings) {
        final CommandHelpBuilder helpBuilder = new CommandHelpBuilder(10, this.getLabel());

        helpBuilder.display(sender, page, Arrays.asList(strings));
    }

    public String getUsageMessage(String subCommand, String... arguments) {
        return Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + this.getLabel() + " " + ChatColor.WHITE + subCommand + " " + String.join(" ", arguments) + ".";
    }

    @Override
    public Plugin getPlugin() {
        return CorePlugin.getInstance();
    }

    public boolean isHidden() {
        return true;
    }
}
