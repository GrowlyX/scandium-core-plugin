package com.solexgames.core.command.impl.moderation;

import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.manager.PlayerManager;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.global.NetworkPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AltsCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (!sender.hasPermission("scandium.command.alts")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player>.");
        }
        if (args.length == 1) {
            final String target = args[0];
            final PotPlayer targetPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

            if (targetPlayer != null) {
                final String playerFormattedDisplay = Color.translate(targetPlayer.getActiveGrant().getRank().getColor() + targetPlayer.getName());

                CompletableFuture<List<Document>> documents = new CompletableFuture<>();
                CompletableFuture.runAsync(() -> {
                    List<Document> documentList = new ArrayList<>();
                    CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("previousIpAddress", targetPlayer.getEncryptedIpAddress())).forEach((Block<? super Document>) documentList::add);
                    documents.complete(documentList);
                });

                documents.thenAcceptAsync(potentialAlts -> {
                    String altsMessage = potentialAlts.stream()
                            .map(this::getFancyName)
                            .collect(Collectors.joining(ChatColor.WHITE + ", "));
                    int altsAmount = potentialAlts.size();

                    sender.sendMessage(new String[]{
                            "",
                            StringUtil.getCentered(playerFormattedDisplay + Color.SECONDARY_COLOR + "'s Alt Accounts " + ChatColor.GRAY + "(x" + altsAmount + "):"),
                            "",
                            StringUtil.getCentered(altsMessage),
                            "",
                            StringUtil.getCentered(ChatColor.GRAY + "[" + ChatColor.GREEN + "Online" + ChatColor.GRAY + "]" + ChatColor.GRAY + " [" + ChatColor.RED + "Offline" + ChatColor.GRAY + "]" + ChatColor.GRAY + " [" + ChatColor.GOLD + "Banned" + ChatColor.GRAY + "]" + ChatColor.GRAY + " [" + ChatColor.DARK_RED + "Blacklisted" + ChatColor.GRAY + "]"),
                            "",
                    });
                });
            } else {
                sender.sendMessage(ChatColor.RED + "That player is not online the network right now.");
            }
        }

        return false;
    }

    private String getFancyName(Document document) {
        StringBuilder stringBuilder = new StringBuilder();

        if (document == null) {
            return stringBuilder.toString();
        }

        if (Bukkit.getPlayerExact(document.getString("name")) != null) {
            stringBuilder.append(ChatColor.GREEN);
        } else {
            stringBuilder.append(ChatColor.RED);
        }

        if (document.getBoolean("restricted") != null) {
            if (document.getBoolean("restricted")) {
                stringBuilder.append(ChatColor.GOLD);
            }
        }

        if (document.getBoolean("blacklisted") != null) {
            if (document.getBoolean("blacklisted")) {
                stringBuilder.append(ChatColor.DARK_RED);
            }
        }

        stringBuilder.append(document.getString("name"));

        return stringBuilder.toString();
    }
}
