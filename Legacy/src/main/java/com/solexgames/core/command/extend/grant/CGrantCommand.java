package com.solexgames.core.command.extend.grant;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.server.Network;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.DateUtil;
import com.solexgames.core.util.StringUtil;
import com.solexgames.core.util.UUIDUtil;
import lombok.SneakyThrows;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CGrantCommand extends BaseCommand {

    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                sender.sendMessage(Color.translate("&cUsage: /" + label + " <player> <rank> <duration> <reason>."));
            }
            if (args.length > 0) {
                if (args.length == 1) {
                    sender.sendMessage(Color.translate("&cUsage: /" + label + " <player> <rank> <duration> <reason>."));
                }
                if (args.length == 2) {
                    sender.sendMessage(Color.translate("&cUsage: /" + label + " <player> <rank> <duration> <reason>."));
                }
                if (args.length > 2) {
                    Document document = CorePlugin.getInstance().getPlayerManager().getDocumentByUuid(UUIDUtil.getId(args[0])).orElse(null);
                    if (document != null) {
                        Rank rank = Rank.getByName(args[1]);
                        Network network = CorePlugin.getInstance().getServerManager().getNetwork();

                        if (rank != null) {
                            UUID uuid = UUIDUtil.getId(args[0]);
                            if (args[2].equalsIgnoreCase("perm") || args[2].equalsIgnoreCase("permanent")) {
                                String reason = StringUtil.buildMessage(args, 3);
                                PotPlayer targetPotPlayer = null;

                                try {
                                    targetPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(document.getString("name"));
                                } catch (Exception ignored) {}

                                Grant newGrant = new Grant(null, rank, System.currentTimeMillis(), -1L, reason, true, true, "global");
                                newGrant.setPermanent(true);
                                newGrant.setIssuedServer(CorePlugin.getInstance().getServerName());

                                if (targetPotPlayer != null) {
                                    targetPotPlayer.getAllGrants().add(newGrant);
                                    targetPotPlayer.setupPlayer();
                                    targetPotPlayer.saveWithoutRemove();

                                    targetPotPlayer.getPlayer().sendMessage(ChatColor.GREEN + Color.translate("Your rank has been set to " + newGrant.getRank().getColor() + newGrant.getRank().getName() + ChatColor.GREEN + "."));
                                    sender.sendMessage(Color.translate(network.getSecondaryColor() + "You've granted " + targetPotPlayer.getPlayer().getDisplayName() + network.getSecondaryColor() + " the rank " + rank.getColor() + rank.getName() + network.getSecondaryColor() + " for " + network.getMainColor() + reason + network.getSecondaryColor() + "."));
                                } else {
                                    List<Grant> allGrants = new ArrayList<>();

                                    if ((!((List<String>) document.get("allGrants")).isEmpty()) || ((document.get("allGrants") != null))) {
                                        List<String> allStringGrants = ((List<String>) document.get("allGrants"));
                                        allStringGrants.forEach(s -> allGrants.add(CorePlugin.GSON.fromJson(s, Grant.class)));
                                    }

                                    allGrants.add(newGrant);

                                    List<String> grantStrings = new ArrayList<>();
                                    allGrants.forEach(grant -> grantStrings.add(grant.toJson()));

                                    document.put("allGrants", grantStrings);

                                    CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("uuid", uuid), document, new ReplaceOptions().upsert(true)));

                                    sender.sendMessage(Color.translate(network.getSecondaryColor() + "You've granted &b" + UUIDUtil.getName(document.getString("uuid")) + network.getSecondaryColor() + " the rank " + rank.getColor() + rank.getName() + network.getSecondaryColor() + " for " + network.getMainColor() + reason + network.getSecondaryColor() + "."));
                                }
                            } else {
                                try {
                                    String reason = StringUtil.buildMessage(args, 3);
                                    PotPlayer targetPotPlayer = null;

                                    try {
                                        targetPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(document.getString("name"));
                                    } catch (Exception ignored) {}

                                    Grant newGrant = new Grant(null, rank, System.currentTimeMillis(), System.currentTimeMillis() - DateUtil.parseDateDiff(args[2], false), reason, true, false, "global");
                                    newGrant.setIssuedServer(CorePlugin.getInstance().getServerName());

                                    if (targetPotPlayer != null) {
                                        targetPotPlayer.getAllGrants().add(newGrant);
                                        targetPotPlayer.setupPlayer();
                                        targetPotPlayer.saveWithoutRemove();

                                        targetPotPlayer.getPlayer().sendMessage(ChatColor.GREEN + Color.translate("Your rank has been set to " + newGrant.getRank().getColor() + newGrant.getRank().getName() + ChatColor.GREEN + "."));
                                        sender.sendMessage(Color.translate(network.getSecondaryColor() + "You've granted " + targetPotPlayer.getPlayer().getDisplayName() + network.getSecondaryColor() + " the rank " + rank.getColor() + rank.getName() + network.getSecondaryColor() + " for " + network.getMainColor() + reason + network.getSecondaryColor() + "."));
                                    } else {
                                        List<Grant> allGrants = new ArrayList<>();

                                        if ((!((List<String>) document.get("allGrants")).isEmpty()) || ((document.get("allGrants") != null))) {
                                            List<String> allStringGrants = ((List<String>) document.get("allGrants"));
                                            allStringGrants.forEach(s -> allGrants.add(CorePlugin.GSON.fromJson(s, Grant.class)));
                                        }

                                        allGrants.add(newGrant);

                                        List<String> grantStrings = new ArrayList<>();
                                        allGrants.forEach(grant -> grantStrings.add(grant.toJson()));

                                        document.put("allGrants", grantStrings);

                                        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("uuid", uuid), document, new ReplaceOptions().upsert(true)));

                                        sender.sendMessage(Color.translate(network.getSecondaryColor() + "You've granted " + args[0] + network.getSecondaryColor() + " the rank " + rank.getColor() + rank.getName() + network.getSecondaryColor() + " for " + network.getMainColor() + reason + network.getSecondaryColor() + "."));
                                    }
                                } catch (Exception exception) {
                                    sender.sendMessage(ChatColor.RED + "Invalid duration.");
                                }
                            }
                        } else {
                            sender.sendMessage(Color.translate("&cThat rank does not exist."));
                        }
                    } else {
                        sender.sendMessage(Color.translate("&cThat player does not exist in our databases."));
                    }
                }
            }
            return false;
        }
        return false;
    }
}
