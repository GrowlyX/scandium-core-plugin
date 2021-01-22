package vip.potclub.core.manager;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.clickable.Clickable;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.punishment.Punishment;
import vip.potclub.core.player.punishment.PunishmentStrings;
import vip.potclub.core.util.CC;
import vip.potclub.core.util.Color;

import java.util.ArrayList;
import java.util.Iterator;

@Getter
@Setter
public class PunishmentManager {

    private final ArrayList<Punishment> punishments = new ArrayList<>();

    public PunishmentManager() {
        CorePlugin.getInstance().getMongoThread().execute(() -> {
            for (Document punishmentDocument : CorePlugin.getInstance().getCoreDatabase().getPunishmentCollection().find()) {
                this.punishments.add(CorePlugin.GSON.fromJson(punishmentDocument.toJson(), Punishment.class));
            }
        });

        CorePlugin.getInstance().getLogger().info("[Punishments] Loaded all punishments.");
    }

    public void savePunishments() {
        for (Document document : CorePlugin.getInstance().getCoreDatabase().getPunishmentCollection().find()) CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPunishmentCollection().deleteOne(document));
        this.punishments.forEach(Punishment::savePunishment);
    }

    public void handlePunishment(Punishment punishment, Player player, Player target, boolean silent) {
        this.punishments.add(punishment);
        if (silent) {
            Bukkit.getOnlinePlayers().forEach(player1 -> {
                if (player1.hasPermission("scandium.staff")) {
                    player1.sendMessage(Color.translate(
                            "&7[S] " + target.getDisplayName() + " &awas " + (punishment.isPermanent() ? "temporarily " : "") + punishment.getPunishmentType().getEdName() + " by &4" + (player != null ? player.getDisplayName() : "&4CONSOLE") + "&a."
                    ));
                }
            });
        } else {
            Bukkit.broadcastMessage(Color.translate(
                    target.getDisplayName() + " &awas " + (punishment.isPermanent() ? "temporarily " : "") + punishment.getPunishmentType().getEdName() + " by &4" + (player != null ? player.getDisplayName() : "&4CONSOLE") + "&a."
            ));
        }
        switch (punishment.getPunishmentType()) {
            case BLACKLIST:
                if (target != null) {
                    PotPlayer potPlayer = PotPlayer.getPlayer(target);
                    if (potPlayer != null) {
                        potPlayer.setBanned(true);
                        target.kickPlayer(Color.translate(PunishmentStrings.BLCK_MESSAGE.replace("<reason>", punishment.getReason())));
                    }
                }
            case IPBAN:
            case BAN:
                if (target != null) {
                    PotPlayer potPlayer = PotPlayer.getPlayer(target);
                    if (potPlayer != null) {
                        potPlayer.setBanned(true);
                        target.kickPlayer((punishment.isPermanent() ? Color.translate(PunishmentStrings.BAN_MESSAGE_PERM.replace("<reason>", punishment.getReason())) : Color.translate(PunishmentStrings.BAN_MESSAGE_TEMP.replace("<reason>", punishment.getReason()).replace("<time>", punishment.getDurationString()))));
                    }
                }
                break;
            case KICK:
                if (target != null) {
                    PotPlayer potPlayer = PotPlayer.getPlayer(target);
                    if (potPlayer != null) {
                        target.kickPlayer(Color.translate(PunishmentStrings.KICK_MESSAGE.replace("<reason>", punishment.getReason())));
                    }
                }
                break;
        }
    }
}
