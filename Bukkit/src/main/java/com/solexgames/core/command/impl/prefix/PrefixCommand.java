package com.solexgames.core.command.impl.prefix;

import com.mongodb.client.model.Filters;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.prefixes.Prefix;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import com.solexgames.core.util.external.impl.PrefixViewPaginatedMenu;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Command(label = "prefix", aliases = {"tags", "tag", "chattags"})
public class PrefixCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length < 2) {
                sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player> <tag>.");
            }
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[0]);

                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "That player does not exist.");
                    return false;
                }

                final Prefix prefix = Prefix.getByName(args[1]);

                if (prefix == null) {
                    sender.sendMessage(ChatColor.RED + "That prefix does not exist!");
                    return false;
                }

                final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

                potPlayer.getAllPrefixes().add(prefix.getName());

                sender.sendMessage(ChatColor.GREEN + "Added the prefix " + prefix.getName() + " to " + ChatColor.GREEN + target.getDisplayName());
                target.sendMessage(ChatColor.GREEN + "You've been given access to the " + prefix.getPrefix() + ChatColor.GREEN + " prefix.");
            }
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            new PrefixViewPaginatedMenu(player).openMenu(player);
        }

        if (!player.hasPermission("scandium.command.prefix")) {
            return false;
        }
        if (args.length > 0) {
            switch (args[0]) {
                case "purchasable":
                    if (args.length == 1)
                        player.sendMessage(ChatColor.RED + ("Usage: /prefix purchasable <name>."));
                    if (args.length == 2) {
                        final String name = args[1];
                        final Prefix rank = Prefix.getByName(name);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getName());

                            if (rank.isPurchasable()) {
                                rank.setPurchasable(false);
                                player.sendMessage(ChatColor.GREEN + Color.translate("Set the " + displayName + ChatColor.GREEN + " prefix purchasable mode to false!"));
                            } else {
                                rank.setPurchasable(true);
                                player.sendMessage(ChatColor.GREEN + Color.translate("Set the " + displayName + ChatColor.GREEN + " prefix purchasable mode to true!"));
                            }

                            rank.savePrefix();
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "create":
                    if (args.length < 3) {
                        this.sendHelp(player);
                    }
                    if (args.length >= 3) {
                        final String name = args[1];
                        final String prefix = StringUtil.buildMessage(args, 2);

                        final Prefix newPrefix = new Prefix(name, prefix);
                        newPrefix.savePrefix();

                        player.sendMessage(ChatColor.GREEN + Color.translate("Created a new prefix with the name &6" + name + ChatColor.GREEN + " and the design &b" + prefix + ChatColor.GREEN + "."));
                    }
                    break;
                case "add":
                    if (args.length < 3) {
                        this.sendHelp(player);
                    }
                    if (args.length >= 3) {
                        final Player target = Bukkit.getPlayer(args[1]);

                        if (target != null) {
                            final Prefix prefix = Prefix.getByName(args[2]);

                            if (prefix != null) {
                                final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

                                potPlayer.getAllPrefixes().add(prefix.getName());
                                player.sendMessage(ChatColor.GREEN + Color.translate("Added the prefix " + prefix.getName() + " to " + target.getDisplayName()));
                            } else {
                                player.sendMessage(ChatColor.RED + ("Error: That prefix does not exist."));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
                        }
                    }
                    break;
                case "remove":
                    if (args.length < 3) {
                        this.sendHelp(player);
                    }
                    if (args.length >= 3) {
                        final Player target = Bukkit.getPlayer(args[1]);

                        if (target != null) {
                            final Prefix prefix = Prefix.getByName(args[2]);

                            if (prefix != null) {
                                final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

                                potPlayer.getAllPrefixes().remove(prefix.getName());
                                CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPrefixCollection().deleteOne(Filters.eq("_id", prefix.getId())));

                                player.sendMessage(ChatColor.GREEN + Color.translate("Removed the prefix " + prefix.getName() + " from " + target.getDisplayName()));
                            } else {
                                player.sendMessage(ChatColor.RED + ("Error: That prefix does not exist."));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
                        }
                    }
                    break;
                case "delete":
                    if (args.length == 1) {
                        this.sendHelp(player);
                    }
                    if (args.length == 2) {
                        final String name = args[1];
                        final Prefix prefix = Prefix.getByName(name);

                        if (prefix != null) {
                            final Document document = CorePlugin.getInstance().getCoreDatabase().getPrefixCollection().find(Filters.eq("name", prefix.getName())).first();

                            if (document != null) {
                                CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPrefixCollection().deleteOne(document));
                            }

                            Prefix.getPrefixes().remove(prefix);
                            CorePlugin.getInstance().getPrefixManager().savePrefixes();

                            player.sendMessage(ChatColor.RED + ("Deleted the prefix with the name '" + name + "'."));
                        }
                    }
                    break;
                case "list":
                    player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                    player.sendMessage(Color.translate(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Prefixes:"));
                    Prefix.getPrefixes().forEach(prefix -> player.sendMessage(Color.translate(" &7- &e" + prefix.getName() + " &7(&d#" + prefix.getId() + "&7) &7(" + prefix.getPrefix() + "&7)")));
                    player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                    break;
                default:
                    this.sendHelp(player);
            }
        }

        return false;
    }

    public void sendHelp(Player player) {
        this.getHelpMessage(1, player,
                "/prefix create <name> <design>",
                "/prefix delete <name>",
                "/prefix add <player> <prefix>",
                "/prefix remove <player> <prefix>",
                "/prefix purchasable <prefix> <boolean>",
                "/prefix list"
        );
    }
}
