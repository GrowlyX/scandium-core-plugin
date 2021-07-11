package com.solexgames.xenon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import com.solexgames.xenon.CorePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author GrowlyX
 * @since 7/10/2021
 */

@CommandAlias("hardmaintenance|hm")
@CommandPermission("xenon.command.maintenance.hard")
public class HardMaintenanceCommand extends BaseCommand {

    @HelpCommand
    public void doHelp(ProxiedPlayer proxiedPlayer, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("toggle confirm")
    @CommandPermission("xenon.command.maintenance.hard.subcommand.toggle")
    public void onToggleConfirm(ProxiedPlayer proxiedPlayer) {
        CorePlugin.getInstance().setHardMaintenance(!CorePlugin.getInstance().isHardMaintenance());

        if (CorePlugin.getInstance().isMaintenance()) {
            proxiedPlayer.sendMessage(ChatColor.GREEN + "You've enabled network hard maintenance and all players who aren't hard whitelisted have been kicked.");

            ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer1 -> {
                if (!CorePlugin.getInstance().getHardWhitelistedPlayers().contains(proxiedPlayer1.getName())) {
                    proxiedPlayer1.disconnect(ChatColor.RED + "Sorry, but the server's now in maintenance.");
                }
            });
        } else {
            proxiedPlayer.sendMessage(ChatColor.GREEN + "You've disabled network hard maintenance!");
        }
    }
}
