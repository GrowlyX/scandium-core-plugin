package com.solexgames.core.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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

public abstract class EBaseCommand implements CommandExecutor {

    protected final String ONLY_PLAYERS = ChatColor.RED + "Only players can execute this command.";
    protected final String NO_PERMISSION = ChatColor.RED + "I'm sorry, but you do not have permission to perform this command.";

    protected abstract boolean execute(CommandSender sender, String label, String[] args);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return this.execute(sender, label, args);
    }
}
