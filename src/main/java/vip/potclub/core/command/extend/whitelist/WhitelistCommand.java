package vip.potclub.core.command.extend.whitelist;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.util.Color;

import java.util.List;

public class WhitelistCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Color.translate("&cUsage: /whitelist <toggle|list|add|remove> <player>"));
        }
        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
            if (args.length > 0) {
                List<String> whitelisted = CorePlugin.getInstance().getServerManager().getWhitelistedPlayers();
                boolean enabled = CorePlugin.getInstance().getWhitelistConfig().getConfiguration().getBoolean("whitelist");
                switch (args[0]) {
                    case "toggle":
                        if (enabled) {
                            CorePlugin.getInstance().getWhitelistConfig().getConfiguration().set("whitelist", false);
                            sender.sendMessage(Color.translate("&cDisabled the whitelist."));
                        } else {
                            CorePlugin.getInstance().getWhitelistConfig().getConfiguration().set("whitelist", true);
                            sender.sendMessage(Color.translate("&aEnabled the whitelist."));
                        }
                        break;
                    case "add":
                        if (args.length == 1) {
                            sender.sendMessage(Color.translate("&cUsage: /whitelist <toggle|add|remove> <player>"));
                        }
                        if (args.length == 2) {
                            String target = args[1];
                            if (whitelisted.contains(target)) {
                                sender.sendMessage(Color.translate("&cThat player is already whitelisted."));
                            } else {
                                whitelisted.add(target);
                                CorePlugin.getInstance().getWhitelistConfig().getConfiguration().set("whitelisted", whitelisted);
                                CorePlugin.getInstance().saveConfig();
                                CorePlugin.getInstance().reloadConfig();
                                sender.sendMessage(Color.translate("&aAdded " + target + " to the whitelist."));
                            }
                        }
                        break;
                    case "remove":
                        if (args.length == 1) {
                            sender.sendMessage(Color.translate("&cUsage: /whitelist <toggle|add|remove> <player>"));
                        }
                        if (args.length == 2) {
                            String target = args[1];
                            if (!whitelisted.contains(target)) {
                                sender.sendMessage(Color.translate("&cThat player is not whitelisted."));
                            } else {
                                whitelisted.remove(target);
                                CorePlugin.getInstance().getWhitelistConfig().getConfiguration().set("whitelisted", whitelisted);
                                CorePlugin.getInstance().saveConfig();
                                CorePlugin.getInstance().reloadConfig();
                                sender.sendMessage(Color.translate("&aRemoved " + target + " from the whitelist."));
                            }
                        }
                        break;
                    case "list":
                        sender.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                        sender.sendMessage(Color.translate("&d&lWhitelisted Players"));
                        whitelisted.forEach(s -> {
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(s);
                            if (offlinePlayer != null) {
                                sender.sendMessage(Color.translate(" &7- " + (offlinePlayer.isOnline() ? "&a" : "&c") + s));
                            } else {
                                sender.sendMessage(Color.translate(" &7- " + "&c" + s));
                            }
                        });
                        sender.sendMessage(Color.translate("&7&M" + StringUtils.repeat("-", 53)));
                        break;
                    default:
                        sender.sendMessage(Color.translate("&cUsage: /whitelist <toggle|list|add|remove> <player>"));
                        break;
                }
            }
        });
        return false;
    }
}
