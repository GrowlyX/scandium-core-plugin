package com.solexgames.core.command;

import com.solexgames.core.util.command.CommandHelpBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * For external plugins which are using this command library.
 * <p>
 * <b>Note:</b> Does not register this command instance on instantiation you will have to register it in your {@link org.bukkit.plugin.java.JavaPlugin} onEnable method manually.
 *
 * @author GrowlyX
 * @since 2021
 * @revised 4/19/2021
 *
 * @see CommandExecutor
 * @see org.bukkit.plugin.java.JavaPlugin
 * @see BaseCommand Original BaseCommand
 */

public abstract class EBaseCommand extends Command {

    protected final String ONLY_PLAYERS = ChatColor.RED + "Only players can execute this command.";
    protected final String NO_PERMISSION = ChatColor.RED + "I'm sorry, but you do not have permission to perform this command.";

    protected EBaseCommand() {
        super("");

        final String commandNameFromClazz = this.getClass().getSimpleName().replace("Command", "").toLowerCase();

        this.setLabel(commandNameFromClazz);
        this.setName(commandNameFromClazz);
        this.setAliases((this.getAliases() == null ? new ArrayList<>() : this.getAliases()));
    }

    public abstract boolean execute(CommandSender sender, String label, String[] args);

    public abstract List<String> getAliases();

    public void getHelpMessage(int page, CommandSender sender, String... strings) {
        final CommandHelpBuilder helpBuilder = new CommandHelpBuilder(10, this.getLabel());

        helpBuilder.display(sender, page, Arrays.asList(strings));
    }

    public boolean isHidden() {
        return true;
    }

    public void registerCommand(Plugin plugin) {
        if (plugin.getServer().getPluginManager() instanceof SimplePluginManager) {
            CommandMap commandMap = null;

            try {
                final Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");

                commandMapField.setAccessible(true);
                commandMap = (CommandMap) commandMapField.get(plugin.getServer().getPluginManager());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (commandMap != null) {
                commandMap.register(plugin.getName(), this);
            } else {
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                plugin.getLogger().warning("Your server software's PluginManager does not contain a commandMap so I cannot register a command. This may be due to the fact you might be running a custom Bukkit/Spigot version.");
            }
        } else {
            plugin.getLogger().warning("Your server software is running a PluginManager that is unrecognized. This may be due to the fact you might be running a custom Bukkit/Spigot version.");
        }
    }
}
