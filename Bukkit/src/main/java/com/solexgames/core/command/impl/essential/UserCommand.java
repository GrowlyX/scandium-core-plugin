package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

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
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("scandium.command.user")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        /*
         * Prepare for spaghetti code!
         */

        if (args.length == 0) {
            sendHelp(sender);
        }
        if (args.length > 0) {
            switch (args[0]) {
                case "permission":
                    if (args.length == 1) {
                        this.sendHelp(sender);
                    }
                    if (args.length > 1) {
                        switch (args[1]) {
                            case "add":
                                if (args.length < 4) {
                                    this.sendHelp(sender);
                                }
                                if (args.length == 4) {
                                    final String permission = args[3];
                                    final Player target = Bukkit.getPlayerExact(args[2]);

                                    if (target != null) {
                                        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

                                        if (potPlayer.getUserPermissions().contains(permission.toLowerCase())) {
                                            sender.sendMessage(Color.translate("&cThat player already has that permission."));
                                        } else {
                                            potPlayer.getUserPermissions().add(permission.toLowerCase());
                                            potPlayer.resetPermissions();
                                            potPlayer.setupPermissions();

                                            sender.sendMessage(ChatColor.GREEN + Color.translate("Added the permission '" + permission + "' to " + target.getDisplayName() + ChatColor.GREEN + "!"));
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "That player does not exist");
                                    }
                                }
                                break;
                            case "remove":
                                if (args.length < 4) {
                                    this.sendHelp(sender);
                                }
                                if (args.length == 4) {
                                    final String permission = args[3];
                                    final Player target = Bukkit.getPlayerExact(args[2]);

                                    if (target != null) {
                                        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

                                        if (!potPlayer.getUserPermissions().contains(permission.toLowerCase())) {
                                            sender.sendMessage(Color.translate("&cThat player does not have that permission."));
                                        } else {
                                            potPlayer.getUserPermissions().remove(permission.toLowerCase());
                                            potPlayer.resetPermissions();
                                            potPlayer.setupPermissions();
                                            sender.sendMessage(ChatColor.GREEN + Color.translate("Removed the permission '" + permission + "' from " + target.getDisplayName() + ChatColor.GREEN + "!"));
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "That player does not exist");
                                    }
                                }
                                break;
                            case "list":
                                if (args.length == 2) {
                                    this.sendHelp(sender);
                                }
                                if (args.length == 3) {
                                    final Player target = Bukkit.getPlayerExact(args[2]);

                                    if (target != null) {
                                        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

                                        if (potPlayer.getUserPermissions().isEmpty()) {
                                            sender.sendMessage(Color.translate("&cThat player does not have any permissions."));
                                        } else {
                                            sender.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                                            sender.sendMessage(Color.translate(NETWORK.getMainColor() + ChatColor.BOLD.toString() + "User Permissions:"));
                                            potPlayer.getUserPermissions().forEach(string -> sender.sendMessage(Color.translate(" &7* &a" + string)));
                                            sender.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "That player does not exist");
                                    }
                                }
                                break;
                        }
                    }
                    break;
                case "disguise":
                    if (args.length < 3) {
                        this.sendHelp(sender);
                    }
                    if (args.length == 3) {
                        final Player target = Bukkit.getPlayerExact(args[1]);
                        final Rank rank = Rank.getByName(args[2]);

                        if (target != null) {
                            final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

                            if (rank != null) {
                                potPlayer.setDisguiseRank(rank);

                                sender.sendMessage(ChatColor.GREEN + "You've disguised " + potPlayer.getColorByRankColor() + potPlayer.getName() + ChatColor.GREEN + " as " + Color.translate(rank.getColor() + rank.getItalic()) + rank.getName() + ChatColor.GREEN + "!");
                                potPlayer.setupPlayerList();

                                CorePlugin.getInstance().getNMS().updateTablist();
                            } else {
                                if (args[2].equalsIgnoreCase("reset")) {
                                    potPlayer.setDisguiseRank(null);

                                    sender.sendMessage(ChatColor.GREEN + "You've undisguised " + potPlayer.getColorByRankColor() + potPlayer.getPlayer().getName());
                                    potPlayer.setupPlayerList();

                                    CorePlugin.getInstance().getNMS().updateTablist();
                                } else {
                                    sender.sendMessage(Color.translate("&cThat rank does not exist."));
                                }
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "That player does not exist");
                        }
                    }
                    break;
                default:
                    this.sendHelp(sender);
                    break;
            }
        }

        return false;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("player");
    }
}
