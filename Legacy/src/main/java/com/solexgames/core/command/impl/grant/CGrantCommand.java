package com.solexgames.core.command.impl.grant;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class CGrantCommand extends BaseCommand {

    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            return false;
        }

        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (args.length <= 2) {
            sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player> <rank> <duration> <reason>."));
        }
        if (args.length > 2) {
            UUID uuid = UUIDUtil.fetchUUID(args[0]);

            if (uuid == null) {
                sender.sendMessage(ChatColor.RED + "Error: That uuid is not valid.");
                return false;
            }

            AtomicReference<Document> document = new AtomicReference<>();
            CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

            CompletableFuture.runAsync(() -> {
                document.set(CorePlugin.getInstance().getPlayerManager().getDocumentByUuid(uuid).orElse(null));
                completableFuture.complete(true);
            });

            completableFuture.thenRun(() -> {
                if (document.get() != null) {
                    Rank rank = Rank.getByName(args[1]);
                    ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

                    if (rank != null) {
                        if (args[2].equalsIgnoreCase("perm") || args[2].equalsIgnoreCase("permanent")) {
                            String reason = StringUtil.buildMessage(args, 3);
                            PotPlayer targetPotPlayer = null;

                            try {
                                targetPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(document.get().getString("name"));
                            } catch (Exception ignored) {
                            }

                            Grant newGrant = new Grant(null, rank, System.currentTimeMillis(), -1L, reason, true, true, "global");
                            newGrant.setPermanent(true);
                            newGrant.setIssuedServer(CorePlugin.getInstance().getServerName());

                            if (targetPotPlayer != null) {
                                targetPotPlayer.getAllGrants().add(newGrant);
                                targetPotPlayer.setupPlayer();
                                targetPotPlayer.saveWithoutRemove();

                                targetPotPlayer.getPlayer().sendMessage(ChatColor.GREEN + Color.translate("Your rank has been set to " + newGrant.getRank().getColor() + newGrant.getRank().getName() + ChatColor.GREEN + "."));
                                sender.sendMessage(Color.translate(network.getSecondaryColor() + "You've granted " + targetPotPlayer.getPlayer().getDisplayName() + network.getSecondaryColor() + " the rank " + rank.getColor() + rank.getItalic() + rank.getName() + network.getSecondaryColor() + " for " + network.getMainColor() + reason + network.getSecondaryColor() + "."));
                            } else {
                                List<Grant> allGrants = new ArrayList<>();

                                if ((!((List<String>) document.get().get("allGrants")).isEmpty()) || ((document.get().get("allGrants") != null))) {
                                    List<String> allStringGrants = ((List<String>) document.get().get("allGrants"));
                                    allStringGrants.forEach(s -> allGrants.add(CorePlugin.GSON.fromJson(s, Grant.class)));
                                }

                                allGrants.add(newGrant);

                                List<String> grantStrings = new ArrayList<>();
                                allGrants.forEach(grant -> grantStrings.add(grant.toJson()));

                                document.get().put("allGrants", grantStrings);

                                CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("uuid", uuid), document.get(), new ReplaceOptions().upsert(true)));

                                sender.sendMessage(Color.translate(network.getSecondaryColor() + "You've granted &b" + UUIDUtil.fetchName(UUID.fromString(document.get().getString("uuid"))) + network.getSecondaryColor() + " the rank " + rank.getColor() + rank.getItalic() + rank.getName() + network.getSecondaryColor() + " for " + network.getMainColor() + reason + network.getSecondaryColor() + "."));
                            }
                        } else {
                            try {
                                String reason = StringUtil.buildMessage(args, 3);
                                PotPlayer targetPotPlayer = null;

                                try {
                                    targetPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(document.get().getString("name"));
                                } catch (Exception ignored) {
                                }

                                Grant newGrant = new Grant(null, rank, System.currentTimeMillis(), System.currentTimeMillis() - DateUtil.parseDateDiff(args[2], false), reason, true, false, "global");
                                newGrant.setIssuedServer(CorePlugin.getInstance().getServerName());

                                if (targetPotPlayer != null) {
                                    targetPotPlayer.getAllGrants().add(newGrant);
                                    targetPotPlayer.setupPlayer();
                                    targetPotPlayer.saveWithoutRemove();

                                    targetPotPlayer.getPlayer().sendMessage(ChatColor.GREEN + Color.translate("Your rank has been set to " + newGrant.getRank().getColor() + newGrant.getRank().getName() + ChatColor.GREEN + "."));
                                    sender.sendMessage(Color.translate(network.getSecondaryColor() + "You've granted " + targetPotPlayer.getPlayer().getDisplayName() + network.getSecondaryColor() + " the rank " + rank.getColor() + rank.getItalic() + rank.getName() + network.getSecondaryColor() + " for " + network.getMainColor() + reason + network.getSecondaryColor() + "."));
                                } else {
                                    List<Grant> allGrants = new ArrayList<>();

                                    if ((!((List<String>) document.get().get("allGrants")).isEmpty()) || ((document.get().get("allGrants") != null))) {
                                        List<String> allStringGrants = ((List<String>) document.get().get("allGrants"));
                                        allStringGrants.forEach(s -> allGrants.add(CorePlugin.GSON.fromJson(s, Grant.class)));
                                    }

                                    allGrants.add(newGrant);

                                    List<String> grantStrings = new ArrayList<>();
                                    allGrants.forEach(grant -> grantStrings.add(grant.toJson()));

                                    document.get().put("allGrants", grantStrings);

                                    CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("uuid", uuid), document.get(), new ReplaceOptions().upsert(true)));

                                    sender.sendMessage(Color.translate(network.getSecondaryColor() + "You've granted " + args[0] + network.getSecondaryColor() + " the rank " + rank.getColor() + rank.getItalic() + rank.getName() + network.getSecondaryColor() + " for " + network.getMainColor() + reason + network.getSecondaryColor() + "."));
                                }
                            } catch (Exception exception) {
                                sender.sendMessage(ChatColor.RED + "Invalid duration.");
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Error: That rank does not exist.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Error: That player does not exist in our database.");
                }
            });
        }
        return false;
    }
}
