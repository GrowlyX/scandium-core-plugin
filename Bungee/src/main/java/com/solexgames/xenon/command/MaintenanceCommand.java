package com.solexgames.xenon.command;

import com.solexgames.xenon.CorePlugin;
import com.solexgames.xenon.util.Color;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import static net.md_5.bungee.api.ChatColor.translateAlternateColorCodes;

/**
 * @author GrowlyX
 * @since 3/5/2021
 */

public class MaintenanceCommand extends Command {

    protected final String ONLY_PLAYERS = ChatColor.RED + "Only players can execute this command.";
    protected final String NO_PERMISSION = ChatColor.RED + "I'm sorry, but you do not have permission to perform this command.";

    public MaintenanceCommand() {
        super("maintenance");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission("xenon.network.maintenance")) {
            if (args.length == 0) {
                sender.sendMessage(TextComponent.fromLegacyText(translateAlternateColorCodes('&', "&3Usage: &b/maintenance &f<enable/disable/add/remove> <player>")));
            }

            if (args.length > 0) {
                switch (args[0]) {
                    case "enable":
                        CorePlugin.getInstance().setMaintenance(true);
                        sender.sendMessage(TextComponent.fromLegacyText(translateAlternateColorCodes('&', "&aEnabled network maintenance.")));
                        break;
                    case "disable":
                        CorePlugin.getInstance().setMaintenance(false);
                        sender.sendMessage(TextComponent.fromLegacyText(translateAlternateColorCodes('&', "&cDisabled network maintenance.")));
                        break;
                    case "toggle":
                        CorePlugin.getInstance().setMaintenance(!CorePlugin.getInstance().isMaintenance());
                        if (CorePlugin.getInstance().isMaintenance()) {
                            sender.sendMessage(TextComponent.fromLegacyText(translateAlternateColorCodes('&', "&aEnabled network maintenance.")));
                        } else {
                            sender.sendMessage(TextComponent.fromLegacyText(translateAlternateColorCodes('&', "&aDisabled network maintenance.")));
                        }
                        break;
                    case "add":
                        if (args.length == 1) {
                            sender.sendMessage(TextComponent.fromLegacyText(translateAlternateColorCodes('&', "&3Usage: &b/maintenance &f<add/remove> <player>")));
                        }
                        if (args.length == 2) {
                            String player = args[1];
                            if (CorePlugin.getInstance().getWhitelistedPlayers().contains(player)) {
                                sender.sendMessage(TextComponent.fromLegacyText(translateAlternateColorCodes('&', "&cThat player is already whitelisted!")));
                            } else {
                                CorePlugin.getInstance().getWhitelistedPlayers().add(player);
                                sender.sendMessage(TextComponent.fromLegacyText(translateAlternateColorCodes('&', "&aAdded &f" + player + "&a to the network maintenance.")));
                            }
                        }
                        break;
                    case "remove":
                        if (args.length == 1) {
                            sender.sendMessage(TextComponent.fromLegacyText(translateAlternateColorCodes('&', "&3Usage: &b/maintenance &f<add/remove> <player>")));
                        }
                        if (args.length == 2) {
                            String player = args[1];
                            if (!CorePlugin.getInstance().getWhitelistedPlayers().contains(player)) {
                                sender.sendMessage(TextComponent.fromLegacyText(translateAlternateColorCodes('&', "&cThat player is not on the maintenance!")));
                            } else {
                                CorePlugin.getInstance().getWhitelistedPlayers().remove(player);
                                sender.sendMessage(TextComponent.fromLegacyText(translateAlternateColorCodes('&', "&aRemoved &f" + player + "&a from the network maintenance.")));
                            }
                        }
                        break;
                    default:
                        sender.sendMessage(TextComponent.fromLegacyText(translateAlternateColorCodes('&', "&3Usage: &b/maintenance &f<enable/disable/add/remove> <player>")));
                        break;
                }
            }
        } else {
            sender.sendMessage(NO_PERMISSION);
        }
    }
}