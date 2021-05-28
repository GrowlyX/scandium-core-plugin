package com.solexgames.core.command.impl.prefix;

import com.mongodb.client.model.Filters;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.prefixes.Prefix;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.StringUtil;
import com.solexgames.core.util.builder.PageListBuilder;
import com.solexgames.core.util.external.impl.PrefixViewPaginatedMenu;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author GrowlyX
 * @since 5/28/2021
 */

@Command(
        label = "prefix",
        aliases = {"tag", "tags", "chattags"},
        hidden = false
)
public class PrefixCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            final Player player = (Player) sender;

            new PrefixViewPaginatedMenu(player).openMenu(player);
            return false;
        }

        if (!sender.hasPermission("scandium.command.prefix")) {
            sender.sendMessage(this.NO_PERMISSION);
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length < 3) {
                    sender.sendMessage(this.getUsageMessage("create", "<name> <design>"));
                }
                if (args.length >= 3) {
                    final String prefixName = args[1];
                    final String prefixDesign = Color.translate(StringUtil.buildMessage(args, 2));
                    final Prefix existingPrefix = Prefix.getByName(prefixName);

                    if (existingPrefix == null) {
                        final Prefix prefix = new Prefix(prefixName, prefixDesign);
                        prefix.savePrefix();

                        sender.sendMessage(Color.SECONDARY_COLOR + "You've created a new prefix with the name " + Color.MAIN_COLOR + prefixName + Color.SECONDARY_COLOR + " and the design " + Color.MAIN_COLOR + prefixDesign + Color.SECONDARY_COLOR + ".");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Error: A prefix with the name " + ChatColor.YELLOW + prefixName + ChatColor.RED + " already exists.");
                    }
                }
                break;
            case "design":
                if (args.length < 3) {
                    sender.sendMessage(this.getUsageMessage("design", "<name> <design>"));
                }
                if (args.length >= 3) {
                    final String prefixName = args[1];
                    final String prefixDesign = Color.translate(StringUtil.buildMessage(args, 2));
                    final Prefix existingPrefix = Prefix.getByName(prefixName);

                    if (existingPrefix != null) {
                        existingPrefix.setPrefix(prefixDesign);
                        existingPrefix.savePrefix();

                        sender.sendMessage(Color.SECONDARY_COLOR + "You've set the design of the " + Color.MAIN_COLOR + prefixName + Color.SECONDARY_COLOR + " prefix to " + Color.MAIN_COLOR + prefixDesign + Color.SECONDARY_COLOR + ".");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Error: The prefix with the name " + ChatColor.YELLOW + prefixName + ChatColor.RED + " does not exist.");
                    }
                }
                break;
            case "add":
                if (args.length < 3) {
                    sender.sendMessage(this.getUsageMessage("add", "<name> <player>"));
                }
                if (args.length >= 3) {
                    final String prefixName = args[1];
                    final Player target = Bukkit.getPlayer(args[2]);
                    final Prefix existingPrefix = Prefix.getByName(prefixName);

                    if (existingPrefix != null) {
                        if (target == null) {
                            sender.sendMessage(ChatColor.RED + "Error: That player does not exist.");
                            return false;
                        }

                        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

                        if (!potPlayer.getAllPrefixes().contains(prefixName)) {
                            potPlayer.getAllPrefixes().add(prefixName);
                            potPlayer.saveWithoutRemove();

                            target.sendMessage(ChatColor.GREEN + "You've been given access to the " + existingPrefix.getPrefix() + ChatColor.GREEN + " prefix.");
                            sender.sendMessage(Color.SECONDARY_COLOR + "You've given " + target.getDisplayName() + Color.SECONDARY_COLOR + " access to the " + Color.MAIN_COLOR + prefixName + Color.SECONDARY_COLOR + " prefix.");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Error: That player already has the " + ChatColor.YELLOW + prefixName + ChatColor.RED + " prefix.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Error: The prefix with the name " + ChatColor.YELLOW + prefixName + ChatColor.RED + " does not exist.");
                    }
                }
                break;
            case "remove":
                if (args.length < 3) {
                    sender.sendMessage(this.getUsageMessage("remove", "<name> <player>"));
                }
                if (args.length >= 3) {
                    final String prefixName = args[1];
                    final Player target = Bukkit.getPlayer(args[2]);
                    final Prefix existingPrefix = Prefix.getByName(prefixName);

                    if (existingPrefix != null) {
                        if (target == null) {
                            sender.sendMessage(ChatColor.RED + "Error: That player does not exist.");
                            return false;
                        }

                        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

                        if (potPlayer.getAllPrefixes().contains(prefixName)) {
                            potPlayer.getAllPrefixes().remove(prefixName);
                            potPlayer.saveWithoutRemove();

                            target.sendMessage(ChatColor.GREEN + "Your access to the " + existingPrefix.getPrefix() + ChatColor.GREEN + " prefix has been removed.");
                            sender.sendMessage(Color.SECONDARY_COLOR + "You've removed " + target.getDisplayName() + "'s" + Color.SECONDARY_COLOR + " access to the " + Color.MAIN_COLOR + prefixName + Color.SECONDARY_COLOR + " prefix.");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Error: That player does not have the " + ChatColor.YELLOW + prefixName + ChatColor.RED + " prefix.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Error: The prefix with the name " + ChatColor.YELLOW + prefixName + ChatColor.RED + " does not exist.");
                    }
                }
                break;
            case "delete":
                if (args.length < 2) {
                    sender.sendMessage(this.getUsageMessage("design", "<name>"));
                }
                if (args.length == 2) {
                    final String prefixName = args[1];
                    final Prefix existingPrefix = Prefix.getByName(prefixName);

                    if (existingPrefix != null) {
                        final Document document = CorePlugin.getInstance().getCoreDatabase().getPrefixCollection()
                                .find(Filters.eq("name", existingPrefix.getName())).first();

                        if (document != null) {
                            CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPrefixCollection().deleteOne(document));
                        }

                        RedisUtil.publishAsync(RedisUtil.deletePrefix(existingPrefix));
                        Prefix.getPrefixes().remove(existingPrefix);

                        sender.sendMessage(Color.SECONDARY_COLOR + "You've deleted the prefix with the name " + Color.MAIN_COLOR + prefixName + Color.SECONDARY_COLOR + ".");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Error: The prefix with the name " + ChatColor.YELLOW + prefixName + ChatColor.RED + " does not exist.");
                    }
                }
                break;
            case "purchasable":
                if (args.length < 2) {
                    sender.sendMessage(this.getUsageMessage("purchasable", "<name>"));
                }
                if (args.length == 2) {
                    final String prefixName = args[1];
                    final Prefix existingPrefix = Prefix.getByName(prefixName);

                    if (existingPrefix != null) {
                        existingPrefix.setPurchasable(!existingPrefix.isPurchasable());
                        existingPrefix.savePrefix();

                        sender.sendMessage(Color.SECONDARY_COLOR + "You've " + (existingPrefix.isPurchasable() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + Color.SECONDARY_COLOR + " purchasable mode for " + Color.MAIN_COLOR + prefixName + Color.SECONDARY_COLOR + ".");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Error: The prefix with the name " + ChatColor.YELLOW + prefixName + ChatColor.RED + " does not exist.");
                    }
                }
                break;
            case "list":
                final PageListBuilder listBuilder = new PageListBuilder(10, "Prefixes");
                final List<String> stringList = Prefix.getPrefixes().stream()
                        .map(prefix -> prefix.getName() + ChatColor.GRAY + " (" + Color.translate(prefix.getPrefix()) + ChatColor.GRAY + ")")
                        .collect(Collectors.toList());

                if (args.length < 2) {
                    listBuilder.display(sender, 1, stringList);
                } else {
                    try {
                        final int integer = Integer.parseInt(args[1]);

                        listBuilder.display(sender, integer, stringList);
                    } catch (Exception ignored) {
                        sender.sendMessage(ChatColor.RED + "Error: That is not a valid integer!");
                    }
                }
                break;
            default:
                this.sendHelp(sender);
                break;
        }

        return false;
    }

    public void sendHelp(CommandSender player) {
        this.getHelpMessage(1, player,
                "/prefix create <name> <design>",
                "/prefix design <name> <design>",
                "/prefix delete <name>",
                "/prefix add <player> <prefix>",
                "/prefix remove <player> <prefix>",
                "/prefix purchasable <prefix>",
                "/prefix list <page>"
        );
    }
}
