package com.solexgames.core.manager;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.global.NetworkPlayer;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentStrings;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import com.solexgames.core.util.RedisUtil;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PunishmentManager {

    @Getter
    private final ArrayList<Punishment> punishments = new ArrayList<>();

    public PunishmentManager() {
        CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPunishmentCollection().find().forEach((Consumer<? super Document>) punishmentDocument -> {
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

    public void handlePunishment(Punishment punishment, String issuer, Document targetDocument, boolean silent) {
        final String name = targetDocument.getString("name");
        final Rank rank = Rank.getByName(targetDocument.getString("rankName"));

        final String playerFormattedName = (rank != null ? rank.getColor() + rank.getItalic() : ChatColor.GRAY) + name;
        final String punishmentExplanation = (!punishment.isPermanent() && !punishment.getPunishmentType().equals(PunishmentType.KICK) ? "temporarily " : "") + punishment.getPunishmentType().getEdName().toLowerCase();
        final String durationFormat = punishment.getPunishmentType().equals(PunishmentType.KICK) ? "." : (punishment.isPermanent() ? "." : " for " + punishment.getDurationString() + ".");

        this.punishments.add(punishment);

        if (issuer != null) {
            final Player issuingPlayer = Bukkit.getPlayer(issuer);

            if (issuingPlayer != null) {
                issuingPlayer.sendMessage(Color.translate((silent ? ChatColor.GRAY + "[Silent] " : "") + ChatColor.GREEN + "You've " + punishmentExplanation + " " + playerFormattedName + ChatColor.GREEN + durationFormat));
            }
        }

        CompletableFuture.runAsync(() -> {
            final Document document = CorePlugin.getInstance().getPlayerManager().getDocumentByName(issuer).orElse(null);

            if (document != null) {
                final Rank playerRank = Rank.getByName(document.getString("rankName"));
                final String formattedName = playerRank.getColor() + playerRank.getItalic() + document.get("name");
                final String issuerName = (issuer != null ? formattedName : "&4Console");

                final String clickableLore = Color.SECONDARY_COLOR + ChatColor.STRIKETHROUGH.toString() + "---------------------------------\n" +
                        Color.SECONDARY_COLOR + "Added by: " + issuerName + "\n" +
                        Color.SECONDARY_COLOR + "Added for: " + ChatColor.WHITE + punishment.getReason() + ChatColor.GRAY + " (" + punishment.getExpirationString() + ")\n" +
                        Color.SECONDARY_COLOR + ChatColor.STRIKETHROUGH.toString() + "---------------------------------";

                if (silent) {
                    PlayerUtil.sendClickableTo("&7[Silent] " + playerFormattedName + " &awas " + punishmentExplanation + " by &4" + issuerName + "&a" + durationFormat,
                            clickableLore,
                            "/c " + targetDocument.getString("name"),
                            ClickEvent.Action.SUGGEST_COMMAND);
                } else {
                    Bukkit.broadcastMessage(Color.translate(
                            playerFormattedName + " &awas " + punishmentExplanation + " by &4" + issuerName + ChatColor.GREEN + "."
                    ));
                }
            } else {
                final String clickableLore = Color.SECONDARY_COLOR + ChatColor.STRIKETHROUGH.toString() + "---------------------------------\n" +
                        Color.SECONDARY_COLOR + "Added by: " + ChatColor.DARK_RED + "Console" + "\n" +
                        Color.SECONDARY_COLOR + "Added for: " + ChatColor.WHITE + punishment.getReason() + ChatColor.GRAY + " (" + punishment.getExpirationString() + ")\n" +
                        Color.SECONDARY_COLOR + ChatColor.STRIKETHROUGH.toString() + "---------------------------------";

                if (silent) {
                    PlayerUtil.sendClickableTo("&7[Silent] " + playerFormattedName + " &awas " + punishmentExplanation + " by &4Console&a" + durationFormat,
                            clickableLore,
                            "/c " + targetDocument.getString("name"),
                            ClickEvent.Action.SUGGEST_COMMAND);
                } else {
                    Bukkit.broadcastMessage(Color.translate(
                            playerFormattedName + " &awas " + punishmentExplanation + " by &4Console&a."
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
                        this.kickWithSameIP(potPlayer.getIpAddress(), Color.translate(PunishmentStrings.BLACK_LIST_MESSAGE.replace("<reason>", punishment.getReason())));
                        break;
                    case IP_BAN:
                        potPlayer.setCurrentlyIpRestricted(true);
                    case BAN:
                        potPlayer.setCurrentlyRestricted(true);
                        this.kickWithSameIP(potPlayer.getIpAddress(), (punishment.isPermanent() ? Color.translate(PunishmentStrings.BAN_MESSAGE_PERM.replace("<id>", punishment.getPunishIdentification()).replace("<reason>", punishment.getReason())) : Color.translate(PunishmentStrings.BAN_MESSAGE_TEMP.replace("<id>", punishment.getPunishIdentification()).replace("<reason>", punishment.getReason()).replace("<time>", punishment.getDurationString()))));
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

    public void handleUnPunishment(Document document, String message, Player player, PunishmentType punishmentType, boolean handleRedis) {
        CompletableFuture.runAsync(() -> {
            final Rank playerRank = Rank.getByName(document.getString("rankName"));
            final String playerName = document.getString("name");
            final UUID playerId = CorePlugin.getInstance().getUuidCache().getUuidFromUsername(playerName);

            if (playerId == null) {
                if (player != null) {
                    player.sendMessage(ChatColor.RED + "Error: That player is not valid!");
                } else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error: That player is not valid!");
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

            final String formattedTarget = Color.translate((playerRank != null ? playerRank.getColor() + playerRank.getItalic() : ChatColor.GRAY) + playerName);
            final String responseMessage = (message.endsWith("-s") ? ChatColor.GRAY + "[Silent] " : "") + ChatColor.GREEN + "You've un" + punishmentType.getEdName().toLowerCase() + " " + formattedTarget + ChatColor.GREEN + " for " + Color.SECONDARY_COLOR + message.replace(" -s", "") + ChatColor.GREEN + ".";

            if (player != null) {
                player.sendMessage(responseMessage);
            } else {
                Bukkit.getConsoleSender().sendMessage(responseMessage);
            }

            punishmentList.stream().findFirst().ifPresent(punishment -> {
                punishment.setRemoved(true);
                punishment.setRemovalReason(message.replace(" -s", ""));
                punishment.setRemover((player != null ? player.getUniqueId() : null));
                punishment.setActive(false);
                punishment.setRemoverName((player != null ? player.getName() : null));

                final String punishmentExplanation = "un" + punishment.getPunishmentType().getEdName().toLowerCase();
                final String issuer = (player != null ? player.getDisplayName() : ChatColor.DARK_RED + "Console");

                if (message.endsWith(" -s")) {
                    final String clickableLore = Color.SECONDARY_COLOR + ChatColor.STRIKETHROUGH.toString() + "---------------------------------\n" +
                            Color.SECONDARY_COLOR + "Removed by: " + issuer + "\n" +
                            Color.SECONDARY_COLOR + "Removed for: " + ChatColor.WHITE + punishment.getRemovalReason() + "\n" +
                            Color.SECONDARY_COLOR + ChatColor.STRIKETHROUGH.toString() + "---------------------------------";

                    PlayerUtil.sendClickableTo("&7[Silent] " + formattedTarget + " &awas " + punishmentExplanation + " by " + issuer + "&a.",
                            clickableLore,
                            null,
                            null);
                } else {
                    Bukkit.broadcastMessage(Color.translate(
                            "&7" + formattedTarget + " &awas " + punishmentExplanation + " by " + issuer + ChatColor.GREEN + "."
                    ));
                }

                if (handleRedis) {
                    punishment.savePunishment();
                    RedisUtil.publishAsync(RedisUtil.removePunishment(player, punishment, message));
                }

                final Player targetPlayer = Bukkit.getPlayer(punishment.getTarget());

                if (targetPlayer != null) {
                    final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(targetPlayer);

                    switch (punishment.getPunishmentType()) {
                        case MUTE:
                            targetPlayer.sendMessage(ChatColor.RED + "You've been un-muted by a staff member.");
                            potPlayer.setCurrentlyMuted(false);
                            break;
                        case WARN:
                            targetPlayer.sendMessage(ChatColor.RED + "Your warning has been removed by a staff member.");
                            break;
                        case BLACKLIST:
                            potPlayer.setCurrentlyBlacklisted(false);
                        case IP_BAN:
                            potPlayer.setCurrentlyIpRestricted(false);
                        case BAN:
                            targetPlayer.sendMessage(ChatColor.RED + "You've been unbanned by a staff member.");
                            potPlayer.setCurrentlyRestricted(false);
                            break;
                    }
                }
            });

            String field = null;

            switch (punishmentType) {
                case BLACKLIST:
                    field = "blacklisted";
                    break;
                case BAN:
                    field = "restricted";
                    break;
                case IP_BAN:
                    field = "ipbanned";
                    break;
            }

            if (field != null) {
                document.replace(field, false);

                CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("uuid", document.getString("uuid")), document, new ReplaceOptions().upsert(true));
            }
        });
    }

    public void kickWithSameIP(String ipAddress, String kickMessage) {
        final Map<UUID, PotPlayer> map = new HashMap<>(CorePlugin.getInstance().getPlayerManager().getAllProfiles());

        map.values().stream().filter(potPlayer -> potPlayer.getIpAddress().equals(ipAddress))
                .forEach(potPlayer -> potPlayer.getPlayer().kickPlayer(kickMessage));
    }
}
