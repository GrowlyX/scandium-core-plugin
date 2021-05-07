package com.solexgames.core.command;

import com.google.common.collect.Sets;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.command.CommandHelpBuilder;
import com.solexgames.core.util.command.CustomHelpTopic;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * @author GrowlyX
 * @since 2021
 *
 * @see CommandExecutor
 * @see CorePlugin
 */

@Getter
public abstract class BaseCommand extends Command {

    protected final String ONLY_PLAYERS = ChatColor.RED + "Only players can execute this command.";
    protected final String NO_PERMISSION = ChatColor.RED + "I'm sorry, but you do not have permission to perform this command.";

    private boolean async;
    private boolean hidden;

    protected BaseCommand() {
        super("");

        final Class<? extends BaseCommand> clazz = this.getClass();
        if (!clazz.isAnnotationPresent(com.solexgames.core.command.annotation.Command.class)) {
            return;
        }

        final com.solexgames.core.command.annotation.Command command = clazz.getAnnotation(com.solexgames.core.command.annotation.Command.class);

        this.async = command.async();
        this.hidden = command.hidden();

        this.setLabel(command.label());
        this.setName(command.label());
        this.setAliases(Arrays.asList(command.aliases().clone()));

        CorePlugin.getInstance().registerCommand(this);
        Bukkit.getHelpMap().addTopic(new CustomHelpTopic(this, Sets.newHashSet(this.getAliases())));
    }

    protected BaseCommand(JavaPlugin javaPlugin) {
        super("");

        final Class<? extends BaseCommand> clazz = this.getClass();
        if (!clazz.isAnnotationPresent(com.solexgames.core.command.annotation.Command.class)) {
            return;
        }

        final com.solexgames.core.command.annotation.Command command = clazz.getAnnotation(com.solexgames.core.command.annotation.Command.class);

        this.async = command.async();
        this.hidden = command.hidden();

        this.setLabel(command.label());
        this.setName(command.label());
        this.setAliases(Arrays.asList(command.aliases().clone()));

        if (javaPlugin.getServer().getPluginManager() instanceof SimplePluginManager) {
            CommandMap commandMap = null;

            try {
                final Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");

                commandMapField.setAccessible(true);
                commandMap = (CommandMap) commandMapField.get(javaPlugin.getServer().getPluginManager());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (commandMap != null) {
                commandMap.register(this.getLabel(), this);
            } else {
                javaPlugin.getServer().getPluginManager().disablePlugin(javaPlugin);
                javaPlugin.getLogger().warning("Your server software's PluginManager does not contain a commandMap so I cannot register a command. This may be due to the fact you might be running a custom Bukkit/Spigot version.");
            }
        } else {
            javaPlugin.getLogger().warning("Your server software is running a PluginManager that is unrecognized. This may be due to the fact you might be running a custom Bukkit/Spigot version.");
        }

        Bukkit.getHelpMap().addTopic(new CustomHelpTopic(this, Sets.newHashSet(this.getAliases())));
    }

    public abstract boolean command(CommandSender sender, String label, String[] args);

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (this.async) {
            CompletableFuture.runAsync(() -> this.command(sender, label, args));
        } else {
            this.command(sender, label, args);
        }

        return true;
    }

    public void getHelpMessage(int page, CommandSender sender, String... strings) {
        final CommandHelpBuilder helpBuilder = new CommandHelpBuilder(10, this.getLabel());

        helpBuilder.display(sender, page, Arrays.asList(strings));
    }

    public String getUsageMessage(String subCommand, String... arguments) {
        return Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + this.getLabel() + " " + ChatColor.WHITE + subCommand + " " + String.join(" ", arguments) + ".";
    }
}
