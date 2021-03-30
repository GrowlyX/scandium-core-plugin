package com.solexgames.core.command.impl.prefix;

import com.mongodb.client.model.Filters;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.prefixes.Prefix;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import com.solexgames.core.util.external.pagination.extend.PrefixViewPaginatedMenu;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrefixCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
            if (args.length < 2) {
                sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player> <tag>."));
            }
            if (args.length == 2) {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target != null) {
                    Prefix prefix = Prefix.getByName(args[1]);
                    if (prefix != null) {
                        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);
                        potPlayer.getAllPrefixes().add(prefix.getName());
                        sender.sendMessage(Color.translate("&aAdded the prefix " + prefix.getName() + " to " + target.getDisplayName()));
                        target.sendMessage(Color.translate("&aYou've been given access to the " + prefix.getPrefix() + "&a prefix."));
                    } else {
                        sender.sendMessage(Color.translate("&cThat prefix does not exist."));
                    }
                } else {
                    sender.sendMessage(Color.translate("&cThat player does not exist."));
                }
            }
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.prefix")) {
            if (args.length == 0) {
                new PrefixViewPaginatedMenu(player).openMenu(player);
            }
            if (args.length > 0) {
                switch (args[0]) {
                    case "create":
                        if (args.length == 1) {
                            this.sendHelp(player);
                        }
                        if (args.length == 2) {
                            this.sendHelp(player);
                        }
                        if (args.length >= 3) {
                            String name = args[1];
                            String prefix = StringUtil.buildMessage(args, 2);

                            Prefix newPrefix = new Prefix(name, prefix);
                            newPrefix.savePrefix();

                            player.sendMessage(Color.translate("&aCreated a new prefix with the name &6" + name + "&a and the design &b" + prefix + "&a."));
                        }
                        break;
                    case "add":
                        if (args.length == 1) {
                            this.sendHelp(player);
                        }
                        if (args.length == 2) {
                            this.sendHelp(player);
                        }
                        if (args.length >= 3) {
                            Player target = Bukkit.getPlayerExact(args[1]);
                            if (target != null) {
                                Prefix prefix = Prefix.getByName(args[2]);
                                if (prefix != null) {
                                    PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);
                                    potPlayer.getAllPrefixes().add(prefix.getName());
                                    player.sendMessage(Color.translate("&aAdded the prefix " + prefix.getName() + " to " + target.getDisplayName()));
                                } else {
                                    player.sendMessage(Color.translate("&cThat prefix does not exist."));
                                }
                            } else {
                                player.sendMessage(Color.translate("&cThat player does not exist."));
                            }
                        }
                        break;
                    case "remove":
                        if (args.length == 1) {
                            this.sendHelp(player);
                        }
                        if (args.length == 2) {
                            this.sendHelp(player);
                        }
                        if (args.length >= 3) {
                            Player target = Bukkit.getPlayerExact(args[1]);
                            if (target != null) {
                                Prefix prefix = Prefix.getByName(args[2]);
                                if (prefix != null) {
                                    PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);
                                    potPlayer.getAllPrefixes().remove(prefix.getName());
                                    CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPrefixCollection().deleteOne(Filters.eq("_id", prefix.getId())));

                                    player.sendMessage(Color.translate("&aRemoved the prefix " + prefix.getName() + " from " + target.getDisplayName()));
                                } else {
                                    player.sendMessage(Color.translate("&cThat prefix does not exist."));
                                }
                            } else {
                                player.sendMessage(Color.translate("&cThat player does not exist."));
                            }
                        }
                        break;
                    case "delete":
                        if (args.length == 1) {
                            this.sendHelp(player);
                        }
                        if (args.length == 2) {
                            String name = args[1];
                            Prefix prefix = Prefix.getByName(name);
                            if (prefix != null) {
                                Document document = CorePlugin.getInstance().getCoreDatabase().getPrefixCollection().find(Filters.eq("name", prefix.getName())).first();
                                if (document != null) {
                                    CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPrefixCollection().deleteOne(document));
                                }
                                Prefix.getPrefixes().remove(prefix);
                                CorePlugin.getInstance().getPrefixManager().savePrefixes();
                                player.sendMessage(Color.translate("&cDeleted the prefix with the name '" + name + "'."));
                            }
                        }
                        break;
                    case "list":
                        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
                        player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                        player.sendMessage(Color.translate(network.getMainColor() + ChatColor.BOLD.toString() + "Prefixes:"));
                        Prefix.getPrefixes().forEach(prefix -> player.sendMessage(Color.translate(" &7- &e" + prefix.getName() + " &7(&d#" + prefix.getId() + "&7) &7(" + prefix.getPrefix() + "&7)")));
                        player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                        break;
                    default:
                        this.sendHelp(player);
                }
            }
        } else {
            new PrefixViewPaginatedMenu(player).openMenu(player);
        }
        return false;
    }

    public void sendHelp(Player player) {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
        player.sendMessage(Color.translate(network.getMainColor() + ChatColor.BOLD.toString() + "Prefix Management:"));
        player.sendMessage(Color.translate("&f/prefix create <name> <prefix> &7- Create a new prefix."));
        player.sendMessage(Color.translate("&f/prefix delete <name> &7- Delete a prefix."));
        player.sendMessage(Color.translate("&f/prefix add <player> <prefix> &7- Add a prefix to a player."));
        player.sendMessage(Color.translate("&f/prefix remove <player> <prefix> &7- Remove a prefix from a player."));
        player.sendMessage(Color.translate("&f/prefix list &7- List all prefixes."));
        player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
    }
}
