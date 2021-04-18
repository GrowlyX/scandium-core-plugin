package com.solexgames.core.manager;

import com.mongodb.Block;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentStrings;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.UUIDUtil;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PunishmentManager {

    @Getter
    private final ArrayList<Punishment> punishments = new ArrayList<>();

    public PunishmentManager() {
        CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPunishmentCollection().find().forEach((Block<? super Document>) punishmentDocument -> {
            final Punishment punishment = new Punishment(
                    PunishmentType.valueOf(punishmentDocument.getString("punishmentType")),
                    (punishmentDocument.getString("issuer") != null ? UUID.fromString(punishmentDocument.getString("issuer")) : null),
                    UUID.fromString(punishmentDocument.getString("target")),
                    punishmentDocument.getString("issuerName"),
                    punishmentDocument.getString("reason"),
                    punishmentDocument.getDate("issuingDate"),
                    punishmentDocument.getLong("punishmentDuration"),
                    punishmentDocument.getBoolean("permanent"),
                    punishmentDocument.getDate("createdAt"),
                    UUID.fromString(punishmentDocument.getString("id")),
                    punishmentDocument.getString("identification"),
                    punishmentDocument.getBoolean("active")
            );

            punishment.setPermanent(punishmentDocument.getBoolean("permanent"));
            punishment.setRemoved(punishmentDocument.getBoolean("removed"));

            if (punishmentDocument.getString("removerName") != null) {
                punishment.setRemoverName(punishmentDocument.getString("removerName"));
            }
            if (punishmentDocument.getString("removalReason") != null) {
                punishment.setRemovalReason(punishmentDocument.getString("removalReason"));
            }
            if (punishmentDocument.getString("remover") != null) {
                punishment.setRemover(UUID.fromString(punishmentDocument.getString("remover")));
            }

            this.punishments.add(punishment);
        }));
    }

    public void savePunishments() {
        this.punishments.forEach(Punishment::saveMainThread);
    }

    public void handlePunishment(Punishment punishment, String issuer, Document targetDocument, boolean silent) {
        final String name = targetDocument.getString("name");
        final Rank rank = Rank.getByName(targetDocument.getString("rankName"));

        this.punishments.add(punishment);

        CompletableFuture.runAsync(() -> {
            final Document document = CorePlugin.getInstance().getPlayerManager().getDocumentByName(issuer).orElse(null);

            if (document != null) {
                final Rank playerRank = Rank.getByName(document.getString("rankName"));
                final String formattedName = playerRank.getColor() + playerRank.getItalic() + document.get("name");

                if (silent) {
                    Bukkit.getOnlinePlayers().stream().filter(player1 -> player1.hasPermission("scandium.staff")).forEach(player1 -> player1.sendMessage(Color.translate(
                            "&7[S] " + (rank != null ? rank.getColor() + rank.getItalic() : ChatColor.GRAY) + name + " &awas " + (!punishment.isPermanent() && !punishment.getPunishmentType().equals(PunishmentType.KICK) ? "temporarily " : "") + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4" + (issuer != null ? formattedName : "&4Console") + ChatColor.GREEN + "."
                    )));
                } else {
                    Bukkit.broadcastMessage(Color.translate(
                            (rank != null ? rank.getColor() + rank.getItalic() : ChatColor.GRAY) + name + " &awas " + (!punishment.isPermanent() && !punishment.getPunishmentType().equals(PunishmentType.KICK) ? "temporarily " : "") + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4" + (issuer != null ? formattedName : "&4Console") + ChatColor.GREEN + "."
                    ));
                }
            } else {
                if (silent) {
                    Bukkit.getOnlinePlayers().stream().filter(player1 -> player1.hasPermission("scandium.staff")).forEach(player1 -> player1.sendMessage(Color.translate(
                            "&7[S] " + (rank != null ? rank.getColor() + rank.getItalic() : ChatColor.GRAY) + name + " &awas " + (!punishment.isPermanent() && !punishment.getPunishmentType().equals(PunishmentType.KICK) ? "temporarily " : "") + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4Console&a."
                    )));
                } else {
                    Bukkit.broadcastMessage(Color.translate(
                            (rank != null ? rank.getColor() + rank.getItalic() : ChatColor.GRAY) + name + " &awas " + (!punishment.isPermanent() && !punishment.getPunishmentType().equals(PunishmentType.KICK) ? "temporarily " : "") + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4Console&a."
                    ));
                }
            }
        });

        Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> {
            final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(name);

            if (potPlayer != null) {
                switch (punishment.getPunishmentType()) {
                    case WARN:
                        potPlayer.getPlayer().sendMessage(ChatColor.RED + "You were warned by a staff member for " + punishment.getReason() + ".");
                        potPlayer.getPlayer().sendMessage(ChatColor.RED + "This punishment will expire in " + ChatColor.RED + ChatColor.BOLD.toString() + punishment.getDurationString() + ChatColor.RED + ".");
                        break;
                    case MUTE:
                        potPlayer.getPlayer().sendMessage(ChatColor.RED + "You were muted by a staff member for " + punishment.getReason() + ".");
                        potPlayer.getPlayer().sendMessage(ChatColor.RED + "This punishment will expire in " + ChatColor.RED + ChatColor.BOLD.toString() + punishment.getDurationString() + ChatColor.RED + ".");
                        potPlayer.setCurrentlyMuted(true);
                        break;
                    case BLACKLIST:
                        potPlayer.setCurrentlyRestricted(true);
                        potPlayer.getPlayer().kickPlayer(Color.translate(PunishmentStrings.BLACK_LIST_MESSAGE.replace("<reason>", punishment.getReason())));
                        break;
                    case IP_BAN:
                    case BAN:
                        potPlayer.setCurrentlyRestricted(true);
                        potPlayer.getPlayer().kickPlayer((punishment.isPermanent() ? Color.translate(PunishmentStrings.BAN_MESSAGE_PERM.replace("<reason>", punishment.getReason())) : Color.translate(PunishmentStrings.BAN_MESSAGE_TEMP.replace("<reason>", punishment.getReason()).replace("<time>", punishment.getDurationString()))));
                        break;
                    case KICK:
                        potPlayer.getPlayer().kickPlayer(Color.translate(PunishmentStrings.KICK_MESSAGE.replace("<reason>", punishment.getReason())));
                        break;
                }
            }
        });

        if (CorePlugin.getInstance().getDiscordManager().getClient() != null) {
            CorePlugin.getInstance().getDiscordManager().sendPunishment(punishment);
        }
    }

    public void handleUnPunishment(Document document, String message, Player player, PunishmentType punishmentType) {
        CompletableFuture.runAsync(() -> {
            final Rank playerRank = Rank.getByName(document.getString("rankName"));
            final String playerName = document.getString("name");
            final UUID playerId = UUIDUtil.fetchUUID(playerName);

            if (playerId == null) {
                if (player != null) {
                    player.sendMessage(ChatColor.RED + "Error: That player is not valid!");
                } else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "That player is not valid!");
                }

                return;
            }

            final List<Punishment> punishmentList = Punishment.getAllPunishments().stream()
                    .filter(punishment -> punishment != null && punishment.getPunishmentType().equals(punishmentType) && punishment.getTarget().toString().equals(playerId.toString()) && punishment.isValid())
                    .sorted(Comparator.comparingLong(Punishment::getCreatedAtLong).reversed())
                    .collect(Collectors.toList());

            final int punishmentAmount = punishmentList.size();

            if (punishmentAmount == 0) {
                if (player != null) {
                    player.sendMessage(ChatColor.RED + "Error: That player is not currently " + punishmentType.getEdName().toLowerCase() + "!");
                } else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "That player is not currently " + punishmentType.getEdName().toLowerCase() + "!");
                }

                return;
            }

            punishmentList.stream().findFirst().ifPresent(punishment -> {
                punishment.setRemoved(true);
                punishment.setRemovalReason(message.replace("-s", ""));
                punishment.setRemover((player != null ? player.getUniqueId() : null));
                punishment.setActive(false);
                punishment.setRemoverName((player != null ? player.getName() : null));

                if (message.endsWith("-s")) {
                    Bukkit.getOnlinePlayers().stream().filter(player1 -> player1.hasPermission("scandium.staff")).forEach(player1 -> player1.sendMessage(Color.translate(
                            "&7[S] " + (playerRank != null ? playerRank.getColor() + playerRank.getItalic() : ChatColor.GRAY) + playerName + " &awas " + "un" + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4" + (player != null ? player.getDisplayName() : "Console") + ChatColor.GREEN + "."
                    )));
                } else {
                    Bukkit.broadcastMessage(Color.translate(
                            "&7" + (playerRank != null ? playerRank.getColor() + playerRank.getItalic() : ChatColor.GRAY) + playerName + " &awas un" + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4" + (player != null ? player.getDisplayName() : "Console") + ChatColor.GREEN + "."
                    ));
                }

                punishment.savePunishment();
                RedisUtil.writeAsync(RedisUtil.removePunishment(player, punishment, message));

                final Player targetPlayer = Bukkit.getPlayer(punishment.getTarget());

                if (targetPlayer != null) {
                    final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(targetPlayer);

                    switch (punishment.getPunishmentType()) {
                        case MUTE:
                            targetPlayer.sendMessage(ChatColor.RED + "You've been unmuted by a staff member.");
                            potPlayer.setCurrentlyMuted(false);
                            break;
                        case WARN:
                            targetPlayer.sendMessage(ChatColor.RED + "Your warning has been removed by a staff member.");
                            break;
                        case BLACKLIST:
                        case IP_BAN:
                        case BAN:
                            targetPlayer.sendMessage(ChatColor.RED + "You've been unrestricted by a staff member.");
                            potPlayer.setCurrentlyRestricted(false);
                            break;
                    }
                }
            });
        });
    }
}
