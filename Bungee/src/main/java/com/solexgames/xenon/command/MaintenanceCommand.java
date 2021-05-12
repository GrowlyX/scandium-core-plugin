package com.solexgames.xenon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import com.solexgames.xenon.CorePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author GrowlyX
 * @since 3/5/2021
 */

@CommandAlias("globalwl|maintenance")
@CommandPermission("xenon.command.maintenance")
public class MaintenanceCommand extends BaseCommand {

    @HelpCommand
    public void doHelp(ProxiedPlayer proxiedPlayer, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("toggle")
    @CommandPermission("xenon.command.maintenance.subcommand.toggle")
    public void onToggle(ProxiedPlayer proxiedPlayer) {
        CorePlugin.getInstance().setMaintenance(!CorePlugin.getInstance().isMaintenance());

        if (CorePlugin.getInstance().isMaintenance()) {
            proxiedPlayer.sendMessage(ChatColor.GREEN + "You've enabled network maintenance!");
        } else {
            proxiedPlayer.sendMessage(ChatColor.GREEN + "You've disabled network maintenance!");
        }
    }

    @Subcommand("add")
    @CommandPermission("xenon.command.maintenance.subcommand.add")
    public void onAdd(ProxiedPlayer proxiedPlayer, String player) {
        if (CorePlugin.getInstance().getWhitelistedPlayers().contains(player)) {
            proxiedPlayer.sendMessage(ChatColor.RED + "I'm sorry, but that player is already on the maintenance list.");
        } else {
            CorePlugin.getInstance().getWhitelistedPlayers().add(player);
            proxiedPlayer.sendMessage(ChatColor.GREEN + "You've added " + ChatColor.YELLOW + player + ChatColor.GREEN + " to the maintenance list.");
        }
    }

    @Subcommand("remove")
    @CommandPermission("xenon.command.maintenance.subcommand.remove")
    public void onRemove(ProxiedPlayer proxiedPlayer, String player) {
        if (!CorePlugin.getInstance().getWhitelistedPlayers().contains(player)) {
            proxiedPlayer.sendMessage(ChatColor.RED + "I'm sorry, but that player is not on the maintenance list.");
        } else {
            CorePlugin.getInstance().getWhitelistedPlayers().remove(player);
            proxiedPlayer.sendMessage(ChatColor.GREEN + "You've removed " + ChatColor.YELLOW + player + ChatColor.GREEN + " from the maintenance list.");
        }
    }
}
