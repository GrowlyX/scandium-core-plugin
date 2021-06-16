package com.solexgames.core.command.impl.rank;

import com.mongodb.client.model.Filters;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.StringUtil;
import com.solexgames.core.util.builder.PageListBuilder;
import com.solexgames.core.util.external.impl.editor.RankEditorMainMenu;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Command(label = "rank", permission = "scandium.command.rank", async = true)
public class RankCommand extends BaseCommand {

    public void sendHelp(Player player, int page) {
        this.getHelpMessage(page, player,
                "/rank editor",
                "/rank create <name>",
                "/rank delete <name>",
                "/rank addperm <name> <permission>",
                "/rank delperm <name> <permission>",
                "/rank addinher <name> <rank>",
                "/rank delinher <name> <rank>",
                "/rank prefix <name> <prefix>",
                "/rank suffix <name> <suffix>",
                "/rank italic <name> <boolean>",
                "/rank hidden <name> <boolean>",
                "/rank default <name> <boolean>",
                "/rank purchasable <name> <boolean>",
                "/rank color <name> <color>",
                "/rank weight <name> <integer>",
                "/rank info <name>",
                "/rank list"
        );
    }

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            this.sendHelp(player, 1);
        }

        if (args.length > 0) {
            try {
                final int page = Integer.parseInt(args[0]);

                this.sendHelp(player, page);

                return true;
            } catch (Exception ignored) {
            }

            switch (args[0]) {
                case "editor":
                    new RankEditorMainMenu().openMenu(player);
                    break;
                case "info":
                    if (args.length == 1) {
                        player.sendMessage(this.getUsageMessage("info", "<name>"));
                    }
                    if (args.length == 2) {
                        final String name = args[1];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                            player.sendMessage(Color.translate(Color.MAIN_COLOR + "&m" + StringUtils.repeat("-", 53)));
                            player.sendMessage(Color.translate(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Rank Information:"));
                            player.sendMessage(Color.translate("  "));
                            player.sendMessage(Color.translate("&7Display: &f" + displayName));
                            player.sendMessage(Color.translate("&7Weight: &f" + rank.getWeight()));
                            player.sendMessage(Color.translate("&7Default: &f" + this.getResult(rank.isDefaultRank())));
                            player.sendMessage(Color.translate("&7Hidden: &f" + this.getResult(rank.isHidden())));
                            player.sendMessage(Color.translate("&7Italic: &f" + this.getResult(rank.isItalic())));
                            player.sendMessage(Color.translate("&7Purchasable: &f" + this.getResult(rank.isPurchasable())));
                            player.sendMessage(Color.translate("&7Prefix: &f" + rank.getPrefix()));
                            player.sendMessage(Color.translate("&7Suffix: &f" + rank.getSuffix()));
                            player.sendMessage(Color.translate("&7ID: &f" + rank.getUuid().toString()));

                            if (!rank.getPermissions().isEmpty()) {
                                player.sendMessage(Color.translate("  "));
                                player.sendMessage(Color.translate(Color.SECONDARY_COLOR + "Permissions:"));
                                rank.getPermissions().forEach(s -> player.sendMessage(Color.translate(" &7* &f" + s)));
                            }

                            if (!rank.getInheritance().isEmpty()) {
                                player.sendMessage(Color.translate("  "));
                                player.sendMessage(Color.translate(Color.SECONDARY_COLOR + "Inheritances:"));

                                rank.getInheritance().stream()
                                        .map(Rank::getByUuid).filter(Objects::nonNull)
                                        .forEach(s -> player.sendMessage(Color.translate(" &7* &f" + s.getColor() + s.getItalic() + s.getName())));
                            }

                            player.sendMessage(Color.translate(Color.MAIN_COLOR + "&m" + StringUtils.repeat("-", 53)));
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "hidden":
                    if (args.length == 1) {
                        player.sendMessage(this.getUsageMessage("hidden", "<name>"));
                    }
                    if (args.length == 2) {
                        final String name = args[1];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                            rank.setHidden(!rank.isHidden());
                            rank.saveRank();

                            RedisUtil.publishAsync(RedisUtil.updateRank(rank));

                            player.sendMessage(Color.SECONDARY_COLOR + "You've toggle hidden mode for the " + displayName + Color.SECONDARY_COLOR + " rank!");
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "default":
                    if (args.length == 1) {
                        player.sendMessage(this.getUsageMessage("default", "<name>"));
                    }
                    if (args.length == 2) {
                        final String name = args[1];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                            rank.setDefaultRank(!rank.isDefaultRank());
                            rank.saveRank();

                            RedisUtil.publishAsync(RedisUtil.updateRank(rank));

                            player.sendMessage(Color.SECONDARY_COLOR + "You've toggled default mode for the " + displayName + Color.SECONDARY_COLOR + " rank!");
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "italic":
                    if (args.length == 1) {
                        player.sendMessage(this.getUsageMessage("italic", "<name>"));
                    }
                    if (args.length == 2) {
                        final String name = args[1];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                            rank.setItalic(!rank.isItalic());
                            rank.saveRank();

                            RedisUtil.publishAsync(RedisUtil.updateRank(rank));

                            player.sendMessage(Color.SECONDARY_COLOR + "You've toggled italic mode for the " + displayName + Color.SECONDARY_COLOR + " rank!");
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "purchasable":
                    if (args.length == 1) {
                        player.sendMessage(this.getUsageMessage("purchasable", "<name>"));
                    }
                    if (args.length == 2) {
                        final String name = args[1];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                            rank.setPurchasable(!rank.isPurchasable());
                            rank.saveRank();

                            RedisUtil.publishAsync(RedisUtil.updateRank(rank));

                            player.sendMessage(Color.SECONDARY_COLOR + "You've toggled purchasable mode for the " + displayName + Color.SECONDARY_COLOR + " rank!");
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "list":
                    final PageListBuilder pageListBuilder = new PageListBuilder(10, "Ranks");
                    final List<String> rankList = CorePlugin.getInstance().getRankManager().getSortedRanks().stream()
                            .map(rank -> Color.translate(rank.getColor() + rank.getItalic() + rank.getName() + " &7(" + rank.getWeight() + ") (" + rank.getPrefix() + "&7)" + " (" + rank.getColor() + rank.getItalic() + "E&7)"))
                            .collect(Collectors.toList());

                    if (args.length == 1) {
                        pageListBuilder.display(sender, 1, rankList);
                    }
                    if (args.length == 2) {
                        try {
                            final int page = Integer.parseInt(args[1]);

                            pageListBuilder.display(sender, page, rankList);

                            return true;
                        } catch (Exception ignored) {
                            player.sendMessage(ChatColor.RED + "Error: That's not a valid integer!");
                        }
                    }
                    break;
                case "delinher":
                    if (args.length < 3) {
                        player.sendMessage(this.getUsageMessage("delinher", "<name>", "<inheritance>"));
                    }
                    if (args.length == 3) {
                        final String name = args[1];
                        final String value = args[2];

                        final Rank rank = Rank.getByName(name);
                        final Rank delRank = Rank.getByName(value);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());
                            final String delDisplayName = Color.translate(delRank.getColor() + delRank.getItalic() + delRank.getName());

                            rank.getInheritance().remove(delRank.getUuid());
                            rank.saveRank();

                            player.sendMessage(Color.SECONDARY_COLOR + "You've removed the inherited rank " + delDisplayName + Color.SECONDARY_COLOR + " from " + displayName + Color.SECONDARY_COLOR + "!");

                            RedisUtil.publishAsync(RedisUtil.updateRank(rank));
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "addinher":
                    if (args.length < 3) {
                        player.sendMessage(this.getUsageMessage("addinher", "<name>", "<inheritance>"));
                    }
                    if (args.length == 3) {
                        final Rank rank = Rank.getByName(args[1]);
                        final Rank addingRank = Rank.getByName(args[2]);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());
                            final String addDisplayName = Color.translate(addingRank.getColor() + addingRank.getItalic() + addingRank.getName());

                            if (!rank.getInheritance().contains(addingRank.getUuid())) {
                                rank.getInheritance().add(addingRank.getUuid());
                                rank.saveRank();

                                player.sendMessage(Color.SECONDARY_COLOR + "You've added the inherited rank " + addDisplayName + Color.SECONDARY_COLOR + " to " + displayName + Color.SECONDARY_COLOR + "!");

                                RedisUtil.publishAsync(RedisUtil.updateRank(rank));
                            } else {
                                player.sendMessage(ChatColor.RED + (displayName + ChatColor.RED + " is already inheriting that rank!"));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "delperm":
                    if (args.length < 3) {
                        player.sendMessage(this.getUsageMessage("delperm", "<name>", "<permission>"));
                    }
                    if (args.length == 3) {
                        final String name = args[1];
                        final String value = args[2];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());
                            final List<String> finalList = new ArrayList<>(rank.getPermissions());

                            finalList.remove(value);

                            rank.setPermissions(finalList);
                            rank.saveRank();

                            player.sendMessage(Color.SECONDARY_COLOR + "You've removed the permission " + Color.MAIN_COLOR + value + Color.SECONDARY_COLOR + " from " + displayName + Color.SECONDARY_COLOR + "!");

                            RedisUtil.publishAsync(RedisUtil.updateRank(rank));
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "addperm":
                    if (args.length < 3) {
                        player.sendMessage(this.getUsageMessage("addperm", "<name>", "<permission>"));
                    }
                    if (args.length == 3) {
                        final String name = args[1];
                        final String value = args[2];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            if (!rank.getPermissions().contains(value)) {
                                final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());
                                final List<String> finalList = new ArrayList<>(rank.getPermissions());

                                finalList.add(value.toLowerCase());

                                rank.setPermissions(finalList);
                                rank.saveRank();

                                player.sendMessage(Color.SECONDARY_COLOR + "You've added the permission " + Color.MAIN_COLOR + value + Color.SECONDARY_COLOR + " to " + displayName + Color.SECONDARY_COLOR + "!");

                                RedisUtil.publishAsync(RedisUtil.updateRank(rank));
                            } else {
                                player.sendMessage(ChatColor.RED + ("Error: That rank already has that permission!"));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "weight":
                    if (args.length < 3) {
                        player.sendMessage(this.getUsageMessage("weight", "<name>", "<integer>"));
                    }
                    if (args.length == 3) {
                        final String name = args[1];
                        final String value = args[2];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            try {
                                final int integer = Integer.parseInt(value);
                                final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                                rank.setWeight(integer);
                                rank.saveRank();

                                player.sendMessage(Color.SECONDARY_COLOR + "You've set the weight of " + displayName + Color.SECONDARY_COLOR + " to " + Color.MAIN_COLOR + integer + Color.SECONDARY_COLOR + "!");

                                RedisUtil.publishAsync(RedisUtil.updateRank(rank));
                            } catch (NumberFormatException exception) {
                                player.sendMessage(ChatColor.RED + ("Error: That's not a valid integer!"));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "color":
                    if (args.length < 3) {
                        player.sendMessage(this.getUsageMessage("color", "<name>", "<color>"));
                    }
                    if (args.length == 3) {
                        final String name = args[1];
                        final String value = args[2];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                            rank.setColor(Color.translate(value));
                            rank.saveRank();

                            player.sendMessage(Color.SECONDARY_COLOR + "You've set the color of " + displayName + Color.SECONDARY_COLOR + " to " + Color.MAIN_COLOR + rank.getColor() + rank.getItalic() + "this" + Color.SECONDARY_COLOR + "!");

                            RedisUtil.publishAsync(RedisUtil.updateRank(rank));
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }

                    break;
                case "suffix":
                    if (args.length < 3) {
                        player.sendMessage(this.getUsageMessage("suffix", "<name>", "<suffix>"));
                    }
                    if (args.length >= 3) {
                        final String value = StringUtil.buildMessage(args, 2);
                        final Rank rank = Rank.getByName(args[1]);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                            rank.setSuffix(Color.translate(value));
                            rank.saveRank();

                            player.sendMessage(Color.SECONDARY_COLOR + "You've set the suffix of " + displayName + Color.SECONDARY_COLOR + " to " + Color.MAIN_COLOR + rank.getSuffix() + Color.SECONDARY_COLOR + "!");

                            RedisUtil.publishAsync(RedisUtil.updateRank(rank));
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }

                    break;
                case "prefix":
                    if (args.length < 3) {
                        player.sendMessage(this.getUsageMessage("prefix", "<name>", "<prefix>"));
                    }
                    if (args.length >= 3) {
                        final String value = StringUtil.buildMessage(args, 2);
                        final Rank rank = Rank.getByName(args[1]);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                            rank.setPrefix(Color.translate(value));
                            rank.saveRank();

                            player.sendMessage(Color.SECONDARY_COLOR + "You've set the prefix of " + displayName + Color.SECONDARY_COLOR + " to " + Color.MAIN_COLOR + rank.getPrefix() + Color.SECONDARY_COLOR + "!");

                            RedisUtil.publishAsync(RedisUtil.updateRank(rank));
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }

                    break;
                case "delete":
                    if (args.length == 1) {
                        player.sendMessage(this.getUsageMessage("delete", "<name>"));
                    }
                    if (args.length == 2) {
                        final String name = args[1];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            RedisUtil.publishAsync(RedisUtil.deleteRank(rank.getName(), player));

                            CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getRankCollection().deleteOne(Filters.eq("_id", rank.getUuid())));
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }

                    break;
                case "create":
                    if (args.length == 1) {
                        player.sendMessage(this.getUsageMessage("create", "<name>"));
                    }
                    if (args.length == 2) {
                        final String name = args[1];

                        if (Rank.getByName(name) != null) {
                            player.sendMessage(ChatColor.RED + ("Error: That rank already exists!"));
                        } else {
                            RedisUtil.publishAsync(RedisUtil.createRank(name, player, UUID.randomUUID().toString()));
                        }
                    }

                    break;
                default:
                    this.sendHelp(player, 1);
                    break;
            }
        }

        return false;
    }

    public String getResult(boolean input) {
        return input ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No";
    }
}
