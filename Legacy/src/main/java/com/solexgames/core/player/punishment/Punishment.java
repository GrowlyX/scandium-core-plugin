package com.solexgames.core.player.punishment;

import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.solexgames.core.CorePlugin;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bson.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * @author GrowlyX
 * @since 2021
 */

@Getter
@Setter
public class Punishment {

    private PunishmentType punishmentType;

    @SerializedName("_id")
    private UUID id;
    private UUID issuer;
    private UUID target;
    private UUID remover;

    private Date expirationDate;
    private Date issuingDate;
    private Date createdAt;

    private String issuerName;
    private String reason;
    private String punishIdentification;
    private String removalReason = null;
    private String removerName = null;

    private boolean active;
    private boolean permanent;
    private boolean removed = false;

    private long punishmentDuration;

    public Punishment(PunishmentType punishmentType, UUID issuer, UUID target, String issuerName, String reason, Date issuingDate, long punishmentDuration, boolean permanent, Date createdAt, UUID uuid, String punishIdentification, boolean active) {
        this.punishmentType = punishmentType;
        this.issuer = issuer;
        this.issuerName = issuerName;
        this.target = target;
        this.reason = reason;
        this.issuingDate = issuingDate;
        this.punishmentDuration = punishmentDuration;
        this.permanent = permanent;
        this.createdAt = createdAt;
        this.expirationDate = new Date(this.createdAt.getTime() + this.punishmentDuration);
        this.id = uuid;
        this.punishIdentification = punishIdentification;
        this.active = active;

        savePunishment();
    }

    public static ArrayList<Punishment> getAllPunishments() {
        return CorePlugin.getInstance().getPunishmentManager().getPunishments();
    }

    public static Punishment getByIdentification(String id) {
        return Punishment.getAllPunishments()
                .stream()
                .filter(punishment -> punishment.getPunishIdentification().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void savePunishment() {
        Document document = new Document("_id", this.id);

        document.put("punishmentType", this.punishmentType.toString());
        document.put("id", this.id.toString());

        if (issuer != null) {
            document.put("issuer", this.issuer.toString());
        } else {
            document.put("issuer", null);
        }

        document.put("target", this.target.toString());
        document.put("expirationDate", this.expirationDate);
        document.put("issuingDate", this.issuingDate);
        document.put("createdAt", this.createdAt);
        document.put("issuerName", this.issuerName);
        document.put("reason", this.reason);
        document.put("remover", (this.remover != null ? this.remover.toString() : null));
        document.put("removalReason", (this.removalReason != null ? this.removalReason : null));
        document.put("removerName", (this.removerName != null ? this.removerName : null));
        document.put("active", this.active);
        document.put("permanent", this.permanent);
        document.put("removed", this.removed);
        document.put("punishmentDuration", this.punishmentDuration);
        document.put("identification", this.punishIdentification);

        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPunishmentCollection().replaceOne(Filters.eq("id", this.id.toString()), document, new ReplaceOptions().upsert(true)));
    }

    public void saveMainThread() {
        Document document = new Document("_id", this.id);

        document.put("punishmentType", this.punishmentType.toString());
        document.put("id", this.id.toString());

        if (issuer != null) {
            document.put("issuer", this.issuer.toString());
        } else {
            document.put("issuer", null);
        }

        document.put("target", this.target.toString());
        document.put("expirationDate", this.expirationDate);
        document.put("issuingDate", this.issuingDate);
        document.put("createdAt", this.createdAt);
        document.put("issuerName", this.issuerName);
        document.put("reason", this.reason);
        document.put("remover", (this.remover != null ? this.remover.toString() : null));
        document.put("removalReason", (this.removalReason != null ? this.removalReason : null));
        document.put("removerName", (this.removerName != null ? this.removerName : null));
        document.put("active", this.active);
        document.put("permanent", this.permanent);
        document.put("removed", this.removed);
        document.put("punishmentDuration", this.punishmentDuration);
        document.put("identification", this.punishIdentification);

        CorePlugin.getInstance().getCoreDatabase().getPunishmentCollection().replaceOne(Filters.eq("id", this.id.toString()), document, new ReplaceOptions().upsert(true));
    }

    public String getDurationString() {
        return this.isPermanent() ? "Permanent" : DurationFormatUtils.formatDurationWords(punishmentDuration, true, true);
    }

    public String getExpirationString() {
        return this.isPermanent() ? "Never" : CorePlugin.FORMAT.format(new Date(this.createdAt.getTime() + this.punishmentDuration));
    }

    public boolean isValid() {
        if (this.isRemoved()) {
            return false;
        } else if (this.isActive()) {
            return true;
        } else if (this.isPermanent()) {
            return true;
        } else {
            return System.currentTimeMillis() >= this.createdAt.getTime() + this.punishmentDuration;
        }
    }

    public long getCreatedAtLong() {
        return this.getCreatedAt().getTime();
    }
}
