package com.solexgames.core.manager;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentStrings;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class PunishmentManager {

    @Getter
    private final ArrayList<Punishment> punishments = new ArrayList<>();

    public PunishmentManager() {

        CorePlugin.getInstance().getMongoThread().execute(() -> {
            for (Document punishmentDocument : CorePlugin.getInstance().getCoreDatabase().getPunishmentCollection().find()) {
                Punishment punishment = new Punishment(
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
            }
        });

        CorePlugin.getInstance().getLogger().info("[Punishments] Loaded all punishments.");
    }

    public void savePunishments() {
        this.punishments.forEach(Punishment::saveMainThread);
    }

    public void handlePunishment(Punishment punishment, String firstPlayer, String target, boolean silent) {
        Bukkit.getScheduler().runTask(CorePlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                CorePlugin.getInstance().getPunishmentManager().getPunishments().add(punishment);

                Player player = null;
                try {
                    player = Bukkit.getPlayerExact(firstPlayer);
                } catch (Exception ignored) { }

                PotPlayer potPlayer = null;
                try {
                    potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);
                } catch (Exception ignored) {}

                PotPlayer finalPotPlayer = potPlayer;
                Player finalPlayer = player;

                if (silent) {
                    Bukkit.getOnlinePlayers().forEach(player1 -> {
                        if (player1.hasPermission("scandium.staff")) {
                            player1.sendMessage(Color.translate(
                                    "&7[S] " + (finalPotPlayer != null ? finalPotPlayer.getPlayer().getDisplayName() : "&7" + target) + " &awas " + (!punishment.isPermanent() ? "temporarily " : "") + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4" + (finalPlayer != null ? finalPlayer.getDisplayName() : (firstPlayer != null ? firstPlayer : "&4Console")) + "&a."
                            ));
                        }
                    });
                } else {
                    Bukkit.broadcastMessage(Color.translate(
                            (finalPotPlayer != null ? finalPotPlayer.getPlayer().getDisplayName() : "&7" + target) + " &awas " + (!punishment.isPermanent() ? "temporarily " : "") + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4" + (finalPlayer != null ? finalPlayer.getDisplayName() : (firstPlayer != null ? firstPlayer : "&4Console")) + "&a."
                    ));
                }
                switch (punishment.getPunishmentType()) {
                    case WARN:
                        if (target != null) {
                            if (finalPotPlayer != null) {
                                finalPotPlayer.getPlayer().sendMessage(Color.translate("  "));
                                finalPotPlayer.getPlayer().sendMessage(Color.translate("&6&l⚠ &c&lYOU WERE WARNED!"));
                                finalPotPlayer.getPlayer().sendMessage(Color.translate("&7Reason: &e" + punishment.getReason()));
                                finalPotPlayer.getPlayer().sendMessage(Color.translate("  "));
                            }
                        }
                        break;
                    case MUTE:
                        if (target != null) {
                            if (finalPotPlayer != null) {
                                finalPotPlayer.getPlayer().sendMessage(Color.translate("&cYou were muted by a staff member."));
                                finalPotPlayer.setCurrentlyMuted(true);
                            }
                        }
                        break;
                    case BLACKLIST:
                        if (target != null) {
                            if (finalPotPlayer != null) {
                                finalPotPlayer.setCurrentlyBanned(true);
                                finalPotPlayer.getPlayer().kickPlayer(Color.translate(PunishmentStrings.BLACK_LIST_MESSAGE.replace("<reason>", punishment.getReason())));
                            }
                        }
                        break;
                    case IPBAN:
                    case BAN:
                        if (target != null) {
                            if (finalPotPlayer != null) {
                                finalPotPlayer.setCurrentlyBanned(true);
                                finalPotPlayer.getPlayer().kickPlayer((punishment.isPermanent() ? Color.translate(PunishmentStrings.BAN_MESSAGE_PERM.replace("<reason>", punishment.getReason())) : Color.translate(PunishmentStrings.BAN_MESSAGE_TEMP.replace("<reason>", punishment.getReason()).replace("<time>", punishment.getDurationString()))));
                            }
                        }
                        break;
                    case KICK:
                        if (target != null) {
                            if (finalPotPlayer != null) {
                                finalPotPlayer.getPlayer().kickPlayer(Color.translate(PunishmentStrings.KICK_MESSAGE.replace("<reason>", punishment.getReason())));
                            }
                        }
                        break;
                }
            }
        }) ;
    }

    public void handleUnpunishment(OfflinePlayer offlinePlayer, String message, Player player, PunishmentType punishmentType) {
        Punishment.getAllPunishments()
                .stream()
                .filter(punishment -> punishment.getTarget().equals(offlinePlayer.getUniqueId()))
                .filter(Punishment::isActive)
                .filter(punishment -> punishment.getPunishmentType().equals(punishmentType))
                .forEach(punishment -> {
                    punishment.setRemoved(true);
                    punishment.setRemovalReason(message.replace("-s", ""));
                    punishment.setRemover(player.getUniqueId());
                    punishment.setActive(false);
                    punishment.setRemoverName(player.getName());

                    if (message.endsWith("-s")) {
                        Bukkit.getOnlinePlayers().forEach(player1 -> {
                            if (player1.hasPermission("scandium.staff")) {
                                player1.sendMessage(Color.translate(
                                        "&7[S] " + offlinePlayer.getName() + " &awas " + "un" + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4" + player.getDisplayName() + "&a."
                                ));
                            }
                        });
                    } else {
                        Bukkit.broadcastMessage(Color.translate(
                                "&7" + offlinePlayer.getName() + " &awas un" + punishment.getPunishmentType().getEdName().toLowerCase() + " by &4" + player.getDisplayName() + "&a."
                        ));
                    }

                    punishment.savePunishment();

                    RedisUtil.writeAsync(RedisUtil.removePunishment(player, punishment, message));
                });
    }
}
