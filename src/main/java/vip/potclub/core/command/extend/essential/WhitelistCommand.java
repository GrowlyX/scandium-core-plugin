package vip.potclub.core.command.extend.essential;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import vip.potclub.core.CorePlugin;

import java.util.List;

public class WhitelistCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /whitelist <toggle|list|add|remove> <player>"));
        }
        if (args.length > 0) {
            List<String> whitelisted = CorePlugin.getInstance().getConfig().getStringList("whitelisted");
            boolean enabled = CorePlugin.getInstance().getConfig().getBoolean("whitelist");
            switch (args[0]) {
                case "toggle":
                    if (enabled) {
                        CorePlugin.getInstance().getConfig().set("whitelist", false);
                        // May be unnecessary, but ¯\_(ツ)_/¯
                        CorePlugin.getInstance().saveConfig();
                        CorePlugin.getInstance().reloadConfig();
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cDisabled the whitelist."));
                    } else {
                        CorePlugin.getInstance().getConfig().set("whitelist", true);
                        CorePlugin.getInstance().saveConfig();
                        CorePlugin.getInstance().reloadConfig();
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aEnabled the whitelist."));
                    }
                    break;
                case "add":
                    if (args.length == 1) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /whitelist <toggle|add|remove> <player>"));
                    }
                    if (args.length == 2) {
                        String target = args[1];
                        if (whitelisted.contains(target)) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat player is already whitelisted."));
                        } else {
                            whitelisted.add(target);
                            CorePlugin.getInstance().getConfig().set("whitelisted", whitelisted);
                            CorePlugin.getInstance().saveConfig();
                            CorePlugin.getInstance().reloadConfig();
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aAdded " + target + " to the whitelist."));
                        }
                    }
                    break;
                case "remove":
                    if (args.length == 1) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /whitelist <toggle|add|remove> <player>"));
                    }
                    if (args.length == 2) {
                        String target = args[1];
                        if (!whitelisted.contains(target)) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat player is not whitelisted."));
                        } else {
                            whitelisted.remove(target);
                            CorePlugin.getInstance().getConfig().set("whitelisted", whitelisted);
                            CorePlugin.getInstance().saveConfig();
                            CorePlugin.getInstance().reloadConfig();
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aRemoved " + target + " from the whitelist."));
                        }
                    }
                    break;
                case "list":
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m" + StringUtils.repeat("-", 53)));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&d&lCURRENTLY WHITELISTED:"));
                    whitelisted.forEach(s -> {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(s);
                        if (offlinePlayer != null) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &7- " + (offlinePlayer.isOnline() ? "&a" : "&c") + s));
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &7- " + "&c" + s));
                        }
                    });
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&M" + StringUtils.repeat("-", 53)));
                    break;
                default:
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /whitelist <toggle|list|add|remove> <player>"));
                    break;
            }
        }
        return false;
    }
}
