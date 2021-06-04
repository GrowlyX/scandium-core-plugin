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
    private boolean consoleOnly;

    private String permissionNode;

    protected BaseCommand() {
        super("");

        final Class<? extends BaseCommand> clazz = this.getClass();
        if (!clazz.isAnnotationPresent(com.solexgames.core.command.annotation.Command.class)) {
            return;
        }

        final com.solexgames.core.command.annotation.Command command = clazz.getAnnotation(com.solexgames.core.command.annotation.Command.class);

        this.async = command.async();
        this.hidden = command.hidden();
        this.consoleOnly = command.consoleOnly();
        this.permissionNode = consoleOnly ? "console" : command.permission();

        this.setPermission(this.permissionNode);
        this.setLabel(command.label());

        try {
            final Field declaredField = Command.class.getDeclaredField("name");

            declaredField.setAccessible(true);
            declaredField.set(this, command.label());
        } catch (Exception exception) {
            return;
        }

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
        this.consoleOnly = command.consoleOnly();
        this.permissionNode = consoleOnly ? "console" : command.permission();

        this.setPermission(this.permissionNode);
        this.setLabel(command.label());

        try {
            final Field declaredField = Command.class.getDeclaredField("name");

            declaredField.setAccessible(true);
            declaredField.set(this, command.label());
        } catch (Exception exception) {
            return;
        }

        this.setAliases(Arrays.asList(command.aliases().clone()));

        this.registerToCommandMap(javaPlugin);
    }

    public void registerToCommandMap(JavaPlugin javaPlugin) {
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
                commandMap.register(javaPlugin.getName(), this);
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

    public boolean canShowOnTabCompletion(CommandSender sender) {
        if (this.permissionNode == null || this.permissionNode.equals("")) {
            return true;
        }

        if (this.hidden) {
            return sender instanceof ConsoleCommandSender || sender.hasPermission(this.permissionNode) || sender.isOp();
        }

        switch (this.permissionNode) {
            case "console":
                return sender instanceof ConsoleCommandSender;
            case "op":
                return sender.isOp();
            case "":
                return true;
        }

        return sender.hasPermission(this.permissionNode);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender) && this.isConsoleOnly()) {
            sender.sendMessage(this.NO_PERMISSION);
            return false;
        }

        if (this.permissionNode != null && !this.permissionNode.equals("") && !sender.hasPermission(this.permissionNode)) {
            sender.sendMessage(this.NO_PERMISSION);
            return false;
        }

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
        return Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + this.getLabel() + " " + ChatColor.WHITE + subCommand + " " + String.join(" ", arguments) + "";
    }
}
