package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UserCommand extends BaseCommand {

    public final ServerType NETWORK = CorePlugin.getInstance().getServerManager().getNetwork();

    public void sendHelp(CommandSender player) {
        player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
        player.sendMessage(Color.translate(NETWORK.getMainColor() + ChatColor.BOLD.toString() + "User Commands:"));
        player.sendMessage(Color.translate("/user permission add <player> &7- Add a permission to a player."));
        player.sendMessage(Color.translate("/user permission remove <player> &7- Remove a permission from a player."));
        player.sendMessage(Color.translate("/user permission list <player> &7- List all player permissions."));
        player.sendMessage(Color.translate("/user disguise <player> <rank> &7- Disguise a player to a rank."));
        player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender.hasPermission("")) {
            if (args.length == 0) {
                sendHelp(sender);
            }
            if (args.length > 0) {
                switch (args[0]) {
                    case "permission":
                        if (args.length == 1) {
                            sendHelp(sender);
                        }
                        if (args.length > 1) {
                            switch (args[1]) {
                                case "add":
                                    if (args.length == 2) {
                                        sendHelp(sender);
                                    }
                                    if (args.length == 3) {
                                        sendHelp(sender);
                                    }
                                    if (args.length == 4) {
                                        String permission = args[3];
                                        Player target = Bukkit.getPlayerExact(args[2]);
                                        if (target != null) {
                                            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);
                                            if (potPlayer.getUserPermissions().contains(permission.toLowerCase())) {
                                                sender.sendMessage(Color.translate("&cThat player already has that permission."));
                                            } else {
                                                potPlayer.getUserPermissions().add(permission.toLowerCase());
                                                potPlayer.resetPermissions();
                                                potPlayer.setupPermissions();
                                                sender.sendMessage(Color.translate("&aAdded the permission '" + permission + "' to " + target.getDisplayName() + "&a!"));
                                            }
                                        } else {
                                            sender.sendMessage(Color.translate("&cThat player does not exist."));
                                        }
                                    }
                                    break;
                                case "remove":
                                    if (args.length == 2) {
                                        sendHelp(sender);
                                    }
                                    if (args.length == 3) {
                                        sendHelp(sender);
                                    }
                                    if (args.length == 4) {
                                        String permission = args[3];
                                        Player target = Bukkit.getPlayerExact(args[2]);
                                        if (target != null) {
                                            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);
                                            if (!potPlayer.getUserPermissions().contains(permission.toLowerCase())) {
                                                sender.sendMessage(Color.translate("&cThat player does not have that permission."));
                                            } else {
                                                potPlayer.getUserPermissions().remove(permission.toLowerCase());
                                                potPlayer.resetPermissions();
                                                potPlayer.setupPermissions();
                                                sender.sendMessage(Color.translate("&aRemoved the permission '" + permission + "' from " + target.getDisplayName() + "&a!"));
                                            }
                                        } else {
                                            sender.sendMessage(Color.translate("&cThat player does not exist."));
                                        }
                                    }
                                    break;
                                case "list":
                                    if (args.length == 2) {
                                        sendHelp(sender);
                                    }
                                    if (args.length == 3) {
                                        Player target = Bukkit.getPlayerExact(args[2]);
                                        if (target != null) {
                                            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);
                                            if (potPlayer.getUserPermissions().isEmpty()) {
                                                sender.sendMessage(Color.translate("&cThat player does not have any permissions."));
                                            } else {
                                                sender.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                                                sender.sendMessage(Color.translate(NETWORK.getMainColor() + ChatColor.BOLD.toString() + "User Commands:"));
                                                potPlayer.getUserPermissions().forEach(string -> sender.sendMessage(Color.translate(" &7* &a" + string)));
                                                sender.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                                            }
                                        } else {
                                            sender.sendMessage(Color.translate("&cThat player does not exist."));
                                        }
                                    }
                                    break;
                            }
                        }
                        break;
                    case "disguise":
                        break;
                }
            }
        } else {
            sender.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}
