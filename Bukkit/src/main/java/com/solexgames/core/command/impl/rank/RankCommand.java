package com.solexgames.core.command.impl.rank;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class RankCommand extends BaseCommand {

    public void sendHelp(Player player) {
        player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
        player.sendMessage(Color.translate(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Rank Management:"));
        player.sendMessage("  ");
        player.sendMessage(Color.translate("/rank create &7- Create a new rank."));
        player.sendMessage(Color.translate("/rank delete &7- Delete an existing rank."));
        player.sendMessage(Color.translate("/rank addPerm &7- Add a permission to a rank."));
        player.sendMessage(Color.translate("/rank delPerm &7- Remove a permission from a rank."));
        player.sendMessage(Color.translate("/rank prefix &7- Set a rank's prefix."));
        player.sendMessage(Color.translate("/rank suffix &7- Set a rank's suffix."));
        player.sendMessage(Color.translate("/rank italic &7- Set a rank as italic."));
        player.sendMessage(Color.translate("/rank color &7- Set a rank's color."));
        player.sendMessage(Color.translate("/rank list &7- List all loaded ranks."));
        player.sendMessage(Color.translate("/rank info &7- Show info of a rank."));
        player.sendMessage(Color.translate("/rank weight &7- Set a rank's weight."));
        player.sendMessage(Color.translate("/rank hidden &7- Set a rank as a hidden rank."));
        player.sendMessage(Color.translate("/rank default &7- Set a rank as a default rank."));
        player.sendMessage(Color.translate("/rank addInher &7- Add an inheritance to a rank."));
        player.sendMessage(Color.translate("/rank delInher &7- Remove an inheritance from a rank."));
        player.sendMessage(Color.translate("/rank purchasable &7- Set a rank purchasable."));
        player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("scandium.command.rank")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            this.sendHelp(player);
        }

        if (args.length > 0) {
            switch (args[0]) {
                case "info":
                    if (args.length == 1) {
                        player.sendMessage(ChatColor.RED + ("Usage: /rank info <name>."));
                    }
                    if (args.length == 2) {
                        final String name = args[1];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            String displayName = Color.translate(rank.getName());

                            player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                            player.sendMessage(Color.translate(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Rank Information:"));
                            player.sendMessage(Color.translate("  "));
                            player.sendMessage(Color.translate("&7Name: &f" + displayName));
                            player.sendMessage(Color.translate("&7Color: &f" + rank.getColor() + rank.getItalic() + "This Color"));
                            player.sendMessage(Color.translate("&7Weight: &f" + rank.getWeight()));
                            player.sendMessage(Color.translate("&7Default: &f" + rank.isDefaultRank()));
                            player.sendMessage(Color.translate("&7Hidden: &f" + rank.isHidden()));
                            player.sendMessage(Color.translate("&7Italic: &f" + rank.isItalic()));
                            player.sendMessage(Color.translate("&7Purchasable: &f" + rank.isPurchasable()));
                            player.sendMessage(Color.translate("&7Prefix: &f" + rank.getPrefix()));
                            player.sendMessage(Color.translate("&7Suffix: &f" + rank.getSuffix()));
                            player.sendMessage(Color.translate("&7UUID: &f" + rank.getUuid().toString()));
                            player.sendMessage(Color.translate("  "));
                            player.sendMessage(Color.translate("&ePermissions:"));
                            rank.getPermissions().forEach(s -> player.sendMessage(Color.translate(" &7* &f" + s)));
                            player.sendMessage(Color.translate("  "));
                            player.sendMessage(Color.translate("&eInheritances:"));
                            rank.getInheritance().stream().map(Rank::getByUuid).filter(Objects::nonNull).forEach(s -> player.sendMessage(Color.translate(" &7* &f" + s.getName())));
                            player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "hidden":
                    if (args.length == 1) player.sendMessage(ChatColor.RED + ("Usage: /rank hidden <name>."));
                    if (args.length == 2) {
                        final String name = args[1];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                            if (rank.isHidden()) {
                                rank.setHidden(false);
                                player.sendMessage(ChatColor.GREEN + Color.translate("Set the " + displayName + ChatColor.GREEN + " rank hidden mode to false!"));
                            } else {
                                rank.setHidden(true);
                                player.sendMessage(ChatColor.GREEN + Color.translate("Set the " + displayName + ChatColor.GREEN + " rank hidden mode to true!"));
                            }

                            RedisUtil.writeAsync(RedisUtil.updateRank(rank));
                            rank.saveRank();
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "default":
                    if (args.length == 1) player.sendMessage(ChatColor.RED + ("Usage: /rank default <name>."));
                    if (args.length == 2) {
                        final String name = args[1];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                            if (rank.isDefaultRank()) {
                                rank.setDefaultRank(false);
                                player.sendMessage(ChatColor.GREEN + Color.translate("Set the " + displayName + ChatColor.GREEN + " rank default mode to false!"));
                            } else {
                                rank.setDefaultRank(true);
                                player.sendMessage(ChatColor.GREEN + Color.translate("Set the " + displayName + ChatColor.GREEN + " rank default mode to true!"));
                            }

                            RedisUtil.writeAsync(RedisUtil.updateRank(rank));
                            rank.saveRank();
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "italic":
                    if (args.length == 1) player.sendMessage(ChatColor.RED + ("Usage: /rank italic <name>."));
                    if (args.length == 2) {
                        final String name = args[1];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                            if (rank.isItalic()) {
                                rank.setItalic(false);
                                player.sendMessage(ChatColor.GREEN + Color.translate("Set the " + displayName + ChatColor.GREEN + " rank italic mode to false!"));
                            } else {
                                rank.setItalic(true);
                                player.sendMessage(ChatColor.GREEN + Color.translate("Set the " + displayName + ChatColor.GREEN + " rank italic mode to true!"));
                            }

                            RedisUtil.writeAsync(RedisUtil.updateRank(rank));
                            rank.saveRank();
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "purchasable":
                    if (args.length == 1) player.sendMessage(ChatColor.RED + ("Usage: /rank purchasable <name>."));
                    if (args.length == 2) {
                        final String name = args[1];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                            if (rank.isPurchasable()) {
                                rank.setPurchasable(false);
                                player.sendMessage(ChatColor.GREEN + Color.translate("Set the " + displayName + ChatColor.GREEN + " rank purchasable mode to false!"));
                            } else {
                                rank.setPurchasable(true);
                                player.sendMessage(ChatColor.GREEN + Color.translate("Set the " + displayName + ChatColor.GREEN + " rank purchasable mode to true!"));
                            }

                            RedisUtil.writeAsync(RedisUtil.updateRank(rank));
                            rank.saveRank();
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "list":
                    player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                    player.sendMessage(Color.translate(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "All Ranks:"));
                    this.getSortedRanks().forEach(rank -> player.sendMessage(Color.translate(" &7* " + Color.translate(rank.getColor() + rank.getItalic() + rank.getName()) + " &7(" + rank.getWeight() + ") (" + rank.getPrefix() + "&7)" + " (" + rank.getColor() + rank.getItalic() + "C&7)")));
                    player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                    break;
                case "teamletter":
                    if (args.length == 1)
                        player.sendMessage(ChatColor.RED + ("Usage: /rank teamLetter <name> <letter>."));
                    if (args.length == 2)
                        player.sendMessage(ChatColor.RED + ("Usage: /rank teamLetter <name> <letter>."));
                    if (args.length == 3) {
                        final String name = args[1];
                        final String value = args[2];

                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                            if (!(value.length() > 1)) {
                                rank.setTeamLetter(value.toLowerCase());
                                player.sendMessage(ChatColor.GREEN + Color.translate("Set the team letter '" + rank.getTeamLetter() + ChatColor.GREEN + "' for the rank " + displayName + ChatColor.GREEN + "."));
                            } else {
                                player.sendMessage(ChatColor.RED + ("Error: That has to be a letter!"));
                            }

                            RedisUtil.writeAsync(RedisUtil.updateRank(rank));
                            rank.saveRank();
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "delinher":
                    if (args.length == 1)
                        player.sendMessage(ChatColor.RED + ("Usage: /rank delinher <name> <inheritance>."));
                    if (args.length == 2)
                        player.sendMessage(ChatColor.RED + ("Usage: /rank delinher <name> <inheritance>."));
                    if (args.length == 3) {
                        final String name = args[1];
                        final String value = args[2];
                        final Rank rank = Rank.getByName(name);
                        final Rank delRank = Rank.getByName(value);

                        if (rank != null) {
                            String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                            rank.getInheritance().remove(delRank.getUuid());
                            player.sendMessage(ChatColor.GREEN + Color.translate("Removed the inheritance '" + delRank.getName() + ChatColor.GREEN + "' from the rank " + displayName + ChatColor.GREEN + "."));

                            RedisUtil.writeAsync(RedisUtil.updateRank(rank));
                            rank.saveRank();
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "addinher":
                    if (args.length == 1)
                        player.sendMessage(ChatColor.RED + ("Usage: /rank addinher <name> <inheritance>."));
                    if (args.length == 2)
                        player.sendMessage(ChatColor.RED + ("Usage: /rank addinher <name> <inheritance>."));
                    if (args.length == 3) {
                        final String name = args[1];
                        final String value = args[2];
                        final Rank rank = Rank.getByName(name);
                        final Rank delRank = Rank.getByName(value);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                            if (!rank.getInheritance().contains(delRank.getUuid())) {
                                rank.getInheritance().add(delRank.getUuid());
                                player.sendMessage(ChatColor.GREEN + Color.translate("Added the inheritance '" + delRank.getName() + ChatColor.GREEN + "' to the rank " + displayName + ChatColor.GREEN + "."));
                            } else {
                                player.sendMessage(ChatColor.RED + ("" + rank.getName() + " is already inheriting that rank!"));
                            }

                            RedisUtil.writeAsync(RedisUtil.updateRank(rank));
                            rank.saveRank();
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "delperm":
                    if (args.length == 1)
                        player.sendMessage(ChatColor.RED + ("Usage: /rank delperm <name> <permission>."));
                    if (args.length == 2)
                        player.sendMessage(ChatColor.RED + ("Usage: /rank delperm <name> <permission>."));
                    if (args.length == 3) {
                        final String name = args[1];
                        final String value = args[2];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());
                            final List<String> finalList = new ArrayList<>(rank.getPermissions());

                            finalList.remove(value);

                            rank.setPermissions(finalList);
                            player.sendMessage(ChatColor.GREEN + Color.translate("Removed the permission '" + value + ChatColor.GREEN + "' from the rank " + displayName + ChatColor.GREEN + "."));

                            RedisUtil.writeAsync(RedisUtil.updateRank(rank));
                            rank.saveRank();
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "addperm":
                    if (args.length == 1)
                        player.sendMessage(ChatColor.RED + ("Usage: /rank addperm <name> <permission>."));
                    if (args.length == 2)
                        player.sendMessage(ChatColor.RED + ("Usage: /rank addperm <name> <permission>."));
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
                                player.sendMessage(ChatColor.GREEN + Color.translate("Added the permission '" + value + ChatColor.GREEN + "' to the rank " + displayName + ChatColor.GREEN + "."));

                                RedisUtil.writeAsync(RedisUtil.updateRank(rank));
                                rank.saveRank();
                            } else {
                                player.sendMessage(ChatColor.RED + ("Error: That rank already has that permission!"));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "weight":
                    if (args.length == 1) player.sendMessage(ChatColor.RED + ("Usage: /rank weight <name> <weight>."));
                    if (args.length == 2) player.sendMessage(ChatColor.RED + ("Usage: /rank weight <name> <weight>."));
                    if (args.length == 3) {
                        final String name = args[1];
                        final String value = args[2];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            try {
                                final int integer = Integer.parseInt(value);
                                final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                                rank.setWeight(integer);
                                player.sendMessage(ChatColor.GREEN + Color.translate("Changed the weight of " + displayName + ChatColor.GREEN + " to &6" + integer + ChatColor.GREEN + "."));

                                RedisUtil.writeAsync(RedisUtil.updateRank(rank));
                                rank.saveRank();
                            } catch (NumberFormatException exception) {
                                player.sendMessage(ChatColor.RED + ("Error: That's not a valid integer!"));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }
                    break;
                case "color":
                    if (args.length == 1) player.sendMessage(ChatColor.RED + ("Usage: /rank color <name> <color>."));
                    if (args.length == 2) player.sendMessage(ChatColor.RED + ("Usage: /rank color <name> <color>."));
                    if (args.length == 3) {
                        final String name = args[1];
                        final String value = args[2];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                            rank.setColor(Color.translate(value));
                            player.sendMessage(ChatColor.GREEN + Color.translate("Changed the color of " + displayName + ChatColor.GREEN + " to " + rank.getColor() + rank.getItalic() + "this" + ChatColor.GREEN + "."));

                            RedisUtil.writeAsync(RedisUtil.updateRank(rank));
                            rank.saveRank();
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }

                    break;
                case "suffix":
                    if (args.length == 1)
                        player.sendMessage(ChatColor.RED + ("Usage: /rank suffix <name> <suffix (_ for space)>."));
                    if (args.length == 2)
                        player.sendMessage(ChatColor.RED + ("Usage: /rank suffix <name> <suffix (_ for space)>."));
                    if (args.length == 3) {
                        final String name = args[1];
                        final String value = args[2];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                            rank.setSuffix(Color.translate(value.replace("_", " ")));
                            player.sendMessage(ChatColor.GREEN + Color.translate("Changed the suffix of " + displayName + ChatColor.GREEN + " to " + rank.getSuffix() + ChatColor.GREEN + "."));

                            RedisUtil.writeAsync(RedisUtil.updateRank(rank));
                            rank.saveRank();
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }

                    break;
                case "prefix":
                    if (args.length == 1)
                        player.sendMessage(ChatColor.RED + ("Usage: /rank prefix <name> <prefix (_ for space)>."));
                    if (args.length == 2)
                        player.sendMessage(ChatColor.RED + ("Usage: /rank prefix <name> <prefix (_ for space)>."));
                    if (args.length == 3) {
                        final String name = args[1];
                        final String value = args[2];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            final String displayName = Color.translate(rank.getColor() + rank.getItalic() + rank.getName());

                            rank.setPrefix(Color.translate(value.replace("_", " ")));
                            player.sendMessage(ChatColor.GREEN + Color.translate("Changed the prefix of " + displayName + ChatColor.GREEN + " to " + rank.getPrefix() + ChatColor.GREEN + "."));

                            RedisUtil.writeAsync(RedisUtil.updateRank(rank));
                            rank.saveRank();
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }

                    break;
                case "delete":
                    if (args.length == 1) player.sendMessage(ChatColor.RED + ("Usage: /rank delete <name>."));
                    if (args.length == 2) {
                        final String name = args[1];
                        final Rank rank = Rank.getByName(name);

                        if (rank != null) {
                            RedisUtil.writeAsync(RedisUtil.deleteRank(rank.getName(), player));
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That rank does not exist!"));
                        }
                    }

                    break;
                case "create":
                    if (args.length == 1) player.sendMessage(ChatColor.RED + ("Usage: /rank create <name>."));
                    if (args.length == 2) {
                        final String name = args[1];

                        if (Rank.getByName(name) != null) {
                            player.sendMessage(ChatColor.RED + ("Error: That rank already exists!"));
                        } else {
                            RedisUtil.writeAsync(RedisUtil.createRank(name, player, UUID.randomUUID().toString()));
                        }
                    }

                    break;
                default:
                    this.sendHelp(player);
                    break;
            }
        }

        return false;
    }

    private List<Rank> getSortedRanks() {
        return Rank.getRanks().stream()
                .sorted(Comparator.comparingInt(Rank::getWeight).reversed())
                .collect(Collectors.toList());
    }
}
