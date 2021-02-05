package vip.potclub.core.command.extend.rank;

import com.mongodb.client.model.Filters;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.enums.ServerType;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.punishment.Punishment;
import vip.potclub.core.player.ranks.Rank;
import vip.potclub.core.util.Color;

import java.util.*;
import java.util.stream.Collectors;

public class RankCommand extends BaseCommand {

    public final ServerType NETWORK = CorePlugin.getInstance().getServerManager().getNetwork();

    public void sendHelp(Player player) {
        player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
        player.sendMessage(Color.translate(NETWORK.getMainColor() + ChatColor.BOLD.toString() + "Rank Management:"));
        player.sendMessage(Color.translate("/rank create &7- Create a new rank."));
        player.sendMessage(Color.translate("/rank delete &7- Delete an existing rank."));
        player.sendMessage(Color.translate("/rank addperm &7- Add a permission to a rank."));
        player.sendMessage(Color.translate("/rank delperm &7- Remove a permission from a rank."));
        player.sendMessage(Color.translate("/rank prefix &7- Set a rank's prefix."));
        player.sendMessage(Color.translate("/rank suffix &7- Set a rank's suffix."));
        player.sendMessage(Color.translate("/rank color &7- Set a rank's color."));
        player.sendMessage(Color.translate("/rank list &7- List all loaded ranks."));
        player.sendMessage(Color.translate("/rank info &7- Show info of a rank."));
        player.sendMessage(Color.translate("/rank weight &7- Set a rank's weight."));
        player.sendMessage(Color.translate("/rank default &7- Set a rank as a default rank."));
        player.sendMessage(Color.translate("/rank addinher &7- Add an inheritance to a rank."));
        player.sendMessage(Color.translate("/rank delinher &7- Remove an inheritance from a rank."));
        player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.rank")) {
            if (args.length == 0) {
                sendHelp(player);
            }
            if (args.length > 0) {
                switch (args[0]) {
                    case "info":
                        if (args.length == 1) player.sendMessage(Color.translate("&cUsage: /rank info <name>."));
                        if (args.length == 2) {
                            String name = args[1];
                            Rank rank = Rank.getByName(name);

                            if (rank != null) {
                                String displayName = Color.translate(rank.getName());

                                player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                                player.sendMessage(Color.translate(NETWORK.getMainColor() + ChatColor.BOLD.toString() + "Rank Information:"));
                                player.sendMessage(Color.translate("  "));
                                player.sendMessage(Color.translate("&7Name: &f" + displayName));
                                player.sendMessage(Color.translate("&7Color: &f" + rank.getColor() + "This Color"));
                                player.sendMessage(Color.translate("&7Weight: &f" + rank.getWeight()));
                                player.sendMessage(Color.translate("&7Suffix: &f" + rank.getSuffix()));
                                player.sendMessage(Color.translate("&7UUID: &f" + rank.getUuid()));
                                player.sendMessage(Color.translate("  "));
                                player.sendMessage(Color.translate("&7Permissions:"));
                                rank.getPermissions().forEach(s -> player.sendMessage(Color.translate(" &7* &f" + s)));
                                player.sendMessage(Color.translate("  "));
                                player.sendMessage(Color.translate("&7Inheritances:"));
                                rank.getInheritance().forEach(s -> player.sendMessage(Color.translate(" &7* &f" + Rank.getByUuid(s).getName())));
                                player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                            } else {
                                player.sendMessage(Color.translate("&cThat rank does not exist!"));
                            }
                        }
                        break;
                    case "list":
                        player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                        player.sendMessage(Color.translate(NETWORK.getMainColor() + ChatColor.BOLD.toString() + "Rank Management:"));
                        this.getSortedRanks().forEach(rank -> {
                            String displayName = Color.translate(rank.getColor() + rank.getName());
                            player.sendMessage(Color.translate(" &7* " + displayName + " &7(" + rank.getWeight() + ") (" + rank.getPrefix() + "&7)" + " (" + rank.getColor() + "C&7)"));
                        });
                        player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                        break;
                    case "delinher":
                        if (args.length == 1) player.sendMessage(Color.translate("&cUsage: /rank delinher <name> <inheritance>."));
                        if (args.length == 2) player.sendMessage(Color.translate("&cUsage: /rank delinher <name> <inheritance>."));
                        if (args.length == 3) {
                            String name = args[1];
                            String value = args[2];
                            Rank rank = Rank.getByName(name);
                            Rank delRank = Rank.getByName(value);

                            if (rank != null) {
                                String displayName = Color.translate(rank.getColor() + rank.getName());

                                rank.getInheritance().remove(delRank.getUuid());
                                player.sendMessage(Color.translate("&aRemoved the inheritance '" + delRank.getName() + "&a' from the rank " + displayName + "&a."));
                            } else {
                                player.sendMessage(Color.translate("&cThat rank does not exist!"));
                            }
                        }
                        break;
                    case "addinher":
                        if (args.length == 1) player.sendMessage(Color.translate("&cUsage: /rank addinher <name> <inheritance>."));
                        if (args.length == 2) player.sendMessage(Color.translate("&cUsage: /rank adddelinher <name> <inheritance>."));
                        if (args.length == 3) {
                            String name = args[1];
                            String value = args[2];
                            Rank rank = Rank.getByName(name);
                            Rank delRank = Rank.getByName(value);

                            if (rank != null) {
                                String displayName = Color.translate(rank.getColor() + rank.getName());

                                if (!rank.getInheritance().contains(delRank.getUuid())) {
                                    rank.getInheritance().add(delRank.getUuid());
                                    player.sendMessage(Color.translate("&aAdded the inheritance '" + delRank.getName() + "&a' to the rank " + displayName + "&a."));
                                } else {
                                    player.sendMessage(Color.translate("&c" + rank.getName() + " is already inheriting that rank!"));
                                }
                            } else {
                                player.sendMessage(Color.translate("&cThat rank does not exist!"));
                            }
                        }
                        break;
                    case "delperm":
                        if (args.length == 1) player.sendMessage(Color.translate("&cUsage: /rank delperm <name> <permission>."));
                        if (args.length == 2) player.sendMessage(Color.translate("&cUsage: /rank delperm <name> <permission>."));
                        if (args.length == 3) {
                            String name = args[1];
                            String value = args[2];
                            Rank rank = Rank.getByName(name);

                            if (rank != null) {
                                String displayName = Color.translate(rank.getColor() + rank.getName());

                                rank.getPermissions().remove(value);
                                player.sendMessage(Color.translate("&aRemoved the permission '" + value + "&a' from the rank " + displayName + "&a."));
                            } else {
                                player.sendMessage(Color.translate("&cThat rank does not exist!"));
                            }
                        }
                        break;
                    case "addperm":
                        if (args.length == 1) player.sendMessage(Color.translate("&cUsage: /rank addperm <name> <permission>."));
                        if (args.length == 2) player.sendMessage(Color.translate("&cUsage: /rank addperm <name> <permission>."));
                        if (args.length == 3) {
                            String name = args[1];
                            String value = args[2];
                            Rank rank = Rank.getByName(name);

                            if (rank != null) {
                                if (!rank.getPermissions().contains(value)) {
                                    String displayName = Color.translate(rank.getColor() + rank.getName());

                                    rank.getPermissions().add(value);
                                    player.sendMessage(Color.translate("&aAdded the permission '" + value + "&a' to the rank " + displayName + "&a."));
                                } else {
                                    player.sendMessage(Color.translate("&cThat rank already has that permission!"));
                                }
                            } else {
                                player.sendMessage(Color.translate("&cThat rank does not exist!"));
                            }
                        }
                        break;
                    case "weight":
                        if (args.length == 1) player.sendMessage(Color.translate("&cUsage: /rank weight <name> <weight>."));
                        if (args.length == 2) player.sendMessage(Color.translate("&cUsage: /rank weight <name> <weight>."));
                        if (args.length == 3) {
                            String name = args[1];
                            String value = args[2];
                            Rank rank = Rank.getByName(name);

                            if (rank != null) {
                                try {
                                    int integer = Integer.parseInt(value);
                                    String displayName = Color.translate(rank.getColor() + rank.getName());

                                    rank.setWeight(integer);
                                    player.sendMessage(Color.translate("&aChanged the weight of " + displayName + "&a to &6" + integer + "&a."));
                                } catch (NumberFormatException exception) {
                                    player.sendMessage(Color.translate("&cThat's not a valid integer!"));
                                }
                            } else {
                                player.sendMessage(Color.translate("&cThat rank does not exist!"));
                            }
                        }
                        break;
                    case "color":
                        if (args.length == 1) player.sendMessage(Color.translate("&cUsage: /rank color <name> <color>."));
                        if (args.length == 2) player.sendMessage(Color.translate("&cUsage: /rank color <name> <color>."));
                        if (args.length == 3) {
                            String name = args[1];
                            String value = args[2];
                            Rank rank = Rank.getByName(name);

                            if (rank != null) {
                                String displayName = Color.translate(rank.getColor() + rank.getName());

                                rank.setColor(Color.translate(value));
                                player.sendMessage(Color.translate("&aChanged the color of " + displayName + "&a to " + rank.getColor() + "this" + "&a."));
                            } else {
                                player.sendMessage(Color.translate("&cThat rank does not exist!"));
                            }
                        }
                        break;
                    case "suffix":
                        if (args.length == 1) player.sendMessage(Color.translate("&cUsage: /rank suffix <name> <suffix>."));
                        if (args.length == 2) player.sendMessage(Color.translate("&cUsage: /rank suffix <name> <suffix>."));
                        if (args.length == 3) {
                            String name = args[1];
                            String value = args[2];
                            Rank rank = Rank.getByName(name);

                            if (rank != null) {
                                String displayName = Color.translate(rank.getColor() + rank.getName());

                                rank.setSuffix(Color.translate(value));
                                player.sendMessage(Color.translate("&aChanged the suffix of " + displayName + "&a to " + rank.getSuffix() + "&a."));
                            } else {
                                player.sendMessage(Color.translate("&cThat rank does not exist!"));
                            }
                        }
                        break;
                    case "prefix":
                        if (args.length == 1) player.sendMessage(Color.translate("&cUsage: /rank prefix <name> <prefix>."));
                        if (args.length == 2) player.sendMessage(Color.translate("&cUsage: /rank prefix <name> <prefix>."));
                        if (args.length == 3) {
                            String name = args[1];
                            String value = args[2];
                            Rank rank = Rank.getByName(name);

                            if (rank != null) {
                                String displayName = Color.translate(rank.getColor() + rank.getName());

                                rank.setPrefix(Color.translate(value));
                                player.sendMessage(Color.translate("&aChanged the prefix of " + displayName + "&a to " + rank.getPrefix() + "&a."));
                            } else {
                                player.sendMessage(Color.translate("&cThat rank does not exist!"));
                            }
                        }
                        break;
                    case "delete":
                        if (args.length == 1) player.sendMessage(Color.translate("&cUsage: /rank delete <name>."));
                        if (args.length == 2) {
                            String name = args[1];
                            Rank rank = Rank.getByName(name);

                            if (rank != null) {
                                Rank.getRanks().remove(rank);
                                CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getRankCollection().deleteOne(Filters.eq("_id", rank.getUuid())));

                                player.sendMessage(Color.translate("&cDeleted the rank '" + name + "'."));
                            } else {
                                player.sendMessage(Color.translate("&cThat rank does not exist!"));
                            }
                        }
                        break;
                    case "create":
                        if (args.length == 1) player.sendMessage(Color.translate("&cUsage: /rank create <name>."));
                        if (args.length == 2) {
                            String name = args[1];

                            if (Rank.getByName(name) != null) {
                                player.sendMessage(Color.translate("&cThat rank already exists!"));
                            } else {
                                Rank rank = new Rank(UUID.randomUUID(), Collections.singletonList(Objects.requireNonNull(Rank.getDefaultRank()).getUuid()), Collections.singletonList("permission.testing"), name, Color.translate("&7"), Color.translate("&7"), Color.translate("&7"), false, 0);
                                rank.saveRank();
                                player.sendMessage(Color.translate("&aCreated a new rank with the name '" + rank.getName() + "'."));
                            }
                        }
                        break;
                    default:
                        sendHelp(player);
                       break;
                 }
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }

    private List<Rank> getSortedRanks() {
        return Rank.getRanks().stream().sorted(Comparator.comparingInt(Rank::getWeight).reversed()).collect(Collectors.toList());
    }
}
