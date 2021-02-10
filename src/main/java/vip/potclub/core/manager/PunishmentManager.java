package vip.potclub.core.manager;

import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.punishment.Punishment;
import vip.potclub.core.player.punishment.PunishmentStrings;
import vip.potclub.core.player.punishment.PunishmentType;
import vip.potclub.core.util.Color;

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
                        punishmentDocument.getString("identification")
                );

                punishment.setActive(punishmentDocument.getBoolean("active"));
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
        this.punishments.add(punishment);

        Player player = null;
        try {
            player = Bukkit.getPlayerExact(firstPlayer);
        } catch (Exception ignored) { }

        PotPlayer potPlayer = null;
        try {
            potPlayer = PotPlayer.getPlayer(target);
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
            case BLACKLIST:
                if (target != null) {
                    if (finalPotPlayer != null) {
                        finalPotPlayer.setCurrentlyBanned(true);
                        finalPotPlayer.getPlayer().kickPlayer(Color.translate(PunishmentStrings.BLCK_MESSAGE.replace("<reason>", punishment.getReason())));
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
}
