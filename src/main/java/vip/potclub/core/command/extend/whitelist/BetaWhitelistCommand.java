package vip.potclub.core.command.extend.whitelist;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.util.Color;

import java.util.List;

public class BetaWhitelistCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Color.translate("&cUsage: /betawl <toggle|allow|list|add|remove> <player>"));
        }
        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
            if (args.length > 0) {
                List<String> whitelisted = CorePlugin.getInstance().getServerManager().getBetaWhitelistedPlayers();
                boolean enabled = CorePlugin.getInstance().getWhitelistConfig().getConfiguration().getBoolean("beta-whitelist");
                switch (args[0]) {
                    case "toggle":
                        if (enabled) {
                            CorePlugin.getInstance().getWhitelistConfig().getConfiguration().set("beta-whitelist", false);
                            sender.sendMessage(Color.translate("&cDisabled the beta whitelist."));
                        } else {
                            CorePlugin.getInstance().getWhitelistConfig().getConfiguration().set("beta-whitelist", true);
                            sender.sendMessage(Color.translate("&aEnabled the beta whitelist."));
                        }
                        break;
                    case "allow":
                        if (CorePlugin.getInstance().getWhitelistConfig().getConfiguration().getBoolean("beta-whitelisted-can-join")) {
                            CorePlugin.getInstance().getWhitelistConfig().getConfiguration().set("beta-whitelisted-can-join", false);
                            sender.sendMessage(Color.translate("&cDisabled the beta whitelist player join."));
                        } else {
                            CorePlugin.getInstance().getWhitelistConfig().getConfiguration().set("beta-whitelisted-can-join", true);
                            sender.sendMessage(Color.translate("&aEnabled the beta whitelist player join."));
                        }
                        break;
                    case "add":
                        if (args.length == 1) {
                            sender.sendMessage(Color.translate("&cUsage: /betawl <toggle|add|remove> <player>"));
                        }
                        if (args.length == 2) {
                            String target = args[1];
                            if (whitelisted.contains(target)) {
                                sender.sendMessage(Color.translate("&cThat player is already beta-whitelisted."));
                            } else {
                                whitelisted.add(target);
                                CorePlugin.getInstance().getWhitelistConfig().getConfiguration().set("beta-whitelisted", whitelisted);
                                CorePlugin.getInstance().saveConfig();
                                CorePlugin.getInstance().reloadConfig();
                                sender.sendMessage(Color.translate("&aAdded " + target + " to the beta-whitelist."));
                            }
                        }
                        break;
                    case "remove":
                        if (args.length == 1) {
                            sender.sendMessage(Color.translate("&cUsage: /betawl <toggle|add|remove> <player>"));
                        }
                        if (args.length == 2) {
                            String target = args[1];
                            if (!whitelisted.contains(target)) {
                                sender.sendMessage(Color.translate("&cThat player is not beta-whitelisted."));
                            } else {
                                whitelisted.remove(target);
                                CorePlugin.getInstance().getWhitelistConfig().getConfiguration().set("beta-whitelisted", whitelisted);
                                CorePlugin.getInstance().saveConfig();
                                CorePlugin.getInstance().reloadConfig();
                                sender.sendMessage(Color.translate("&aRemoved " + target + " from the beta-whitelist."));
                            }
                        }
                        break;
                    case "list":
                        sender.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                        sender.sendMessage(Color.translate("&d&lBeta Whitelisted Players"));
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
                        sender.sendMessage(Color.translate("&cUsage: /betawl <toggle|list|add|remove> <player>"));
                        break;
                }
            }
        });
        return false;
    }
}
