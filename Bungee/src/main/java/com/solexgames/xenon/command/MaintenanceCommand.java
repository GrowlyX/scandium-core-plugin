package com.solexgames.xenon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.solexgames.xenon.CorePlugin;
import com.solexgames.xenon.redis.json.JsonAppender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author GrowlyX
 * @since 3/5/2021
 */

@CommandAlias("maintenance")
@CommandPermission("xenon.command.maintenance")
public class MaintenanceCommand extends BaseCommand {

    @Default
    @HelpCommand
    @Syntax("[help]")
    public void doHelp(ProxiedPlayer proxiedPlayer, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("toggle")
    @Description("Toggle xenon maintenance mode.")
    @CommandPermission("xenon.command.maintenance.subcommand.toggle")
    public void onToggle(ProxiedPlayer proxiedPlayer) {
        proxiedPlayer.sendMessage(ChatColor.RED + "Please use " + ChatColor.YELLOW + "/maintenance toggle confirm" + ChatColor.RED + " to confirm this action.");
    }

    @Subcommand("list")
    @Description("List all whitelisted players.")
    public void onList(ProxiedPlayer proxiedPlayer) {
        final int blockedLogins = CorePlugin.getInstance().getWhitelistedPlayers().size();

        proxiedPlayer.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Xenon Maintenance Information:");
        proxiedPlayer.sendMessage(ChatColor.WHITE.toString() + blockedLogins + ChatColor.GRAY + " player" + (blockedLogins == 1 ? "" : "s") + " are whitelisted.");
        proxiedPlayer.sendMessage(" ");

        CorePlugin.getInstance().getWhitelistedPlayers().forEach(s -> {
            if (!s.equals("GrowlyX")) {
                proxiedPlayer.sendMessage(ChatColor.GRAY + " - " + ChatColor.YELLOW + s);
            }
        });
    }

    @Private
    @Subcommand("toggle confirm")
    @CommandPermission("xenon.command.maintenance.subcommand.toggle")
    public void onToggleConfirm(ProxiedPlayer proxiedPlayer) {
        CorePlugin.getInstance().setMaintenance(!CorePlugin.getInstance().isMaintenance());

        if (CorePlugin.getInstance().isMaintenance()) {
            proxiedPlayer.sendMessage(ChatColor.GREEN + "You've enabled network maintenance and all players who aren't whitelisted have been kicked.");
        } else {
            proxiedPlayer.sendMessage(ChatColor.GREEN + "You've disabled network maintenance!");
        }

        CorePlugin.getInstance().getJedisManager().publish(
                new JsonAppender("MAINTENANCE_UPDATE")
                        .put("TYPE", CorePlugin.getInstance().isMaintenance())
                        .getAsJson()
        );
    }

    @Subcommand("add")
    @Syntax("<player>")
    @Description("Add a player to maintenance globally.")
    @CommandPermission("xenon.command.maintenance.subcommand.add")
    public void onAdd(ProxiedPlayer proxiedPlayer, String player) {
        if (CorePlugin.getInstance().getWhitelistedPlayers().contains(player)) {
            proxiedPlayer.sendMessage(ChatColor.RED + "I'm sorry, but that player is already on the maintenance list.");
        } else {
            CorePlugin.getInstance().getJedisManager().publish(
                    new JsonAppender("MAINTENANCE_ADD")
                            .put("PLAYER", player)
                            .getAsJson()
            );

            proxiedPlayer.sendMessage(ChatColor.GREEN + "You've added " + ChatColor.YELLOW + player + ChatColor.GREEN + " to the maintenance list.");
        }
    }

    @Subcommand("remove")
    @Syntax("<player>")
    @Description("Remove a player from maintenance globally.")
    @CommandPermission("xenon.command.maintenance.subcommand.remove")
    public void onRemove(ProxiedPlayer proxiedPlayer, String player) {
        if (!CorePlugin.getInstance().getWhitelistedPlayers().contains(player)) {
            proxiedPlayer.sendMessage(ChatColor.RED + "I'm sorry, but that player is not on the maintenance list.");
        } else {
            CorePlugin.getInstance().getJedisManager().publish(
                    new JsonAppender("MAINTENANCE_REMOVE")
                            .put("PLAYER", player)
                            .getAsJson()
            );

            proxiedPlayer.sendMessage(ChatColor.GREEN + "You've removed " + ChatColor.YELLOW + player + ChatColor.GREEN + " from the maintenance list.");
        }
    }
}
