package vip.potclub.core.player.punishment;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bson.Document;
import vip.potclub.core.CorePlugin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class Punishment {

    @Getter
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private PunishmentType punishmentType;

    private UUID id;
    private UUID issuer;
    private UUID target;
    private UUID remover;

    private Date expirationDate;
    private Date issuingDate;
    private Date createdAt;

    private String issuerName;
    private String reason;
    private String removalReason = null;
    private String removerName = null;

    private boolean active = true;
    private boolean permanent;
    private boolean removed = false;

    private long punishmentDuration;

    public Punishment(PunishmentType punishmentType, UUID issuer, UUID target, String issuerName, String reason, Date issuingDate, long punishmentDuration, boolean permanent, Date createdAt) {
        this.punishmentType = punishmentType;
        this.issuer = issuer;
        this.issuerName = issuerName;
        this.target = target;
        this.reason = reason;
        this.issuingDate = issuingDate;
        this.punishmentDuration = punishmentDuration;
        this.permanent = permanent;
        this.createdAt = createdAt;

        this.id = UUID.randomUUID();

        savePunishment();
    }

    public boolean isPermanent() {
        return this.punishmentDuration == -1L;
    }

    public boolean isRemoved() {
        return this.removalReason != null;
    }

    public String getDurationString() {
        return this.isPermanent() ? "Permanent" : DurationFormatUtils.formatDurationWords(punishmentDuration, true, true);
    }

    public String getExpirationString() {
        return this.isPermanent() ? "Never" : DATE_FORMAT.format(new Date(this.createdAt.getTime() + this.punishmentDuration));
    }

    public boolean isActive() {
        if (this.isRemoved()) {
            return false;
        } else if (this.isPermanent()) {
            return true;
        } else {
            return System.currentTimeMillis() < this.createdAt.getTime() + punishmentDuration;
        }
    }

    public void savePunishment() {
        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPunishmentCollection().replaceOne(Filters.eq("_id", this.id), Document.parse(CorePlugin.GSON.toJson(this)), new ReplaceOptions().upsert(true)));
    }

    public void saveMainThread() {
        CorePlugin.getInstance().getCoreDatabase().getPunishmentCollection().replaceOne(Filters.eq("_id", this.id), Document.parse(CorePlugin.GSON.toJson(this)), new ReplaceOptions().upsert(true));
    }

    public static ArrayList<Punishment> getAllPunishments() {
        return CorePlugin.getInstance().getPunishmentManager().getPunishments();
    }
}
