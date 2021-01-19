package vip.potclub.core.player.punishment;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import vip.potclub.core.CorePlugin;

import java.util.*;

@Getter
@Setter
public class Punishment {

    private PunishmentType punishmentType;
    private PunishmentDuration punishmentDuration;

    private UUID id;

    private Date expirationDate;
    private Date issuingDate;

    private String issuer;
    private String reason;

    private boolean active;

    public Punishment(PunishmentType punishmentType, String issuer, String reason, Date issuingDate, PunishmentDuration punishmentDuration) {
        this.punishmentType = punishmentType;
        this.issuer = issuer;
        this.reason = reason;
        this.issuingDate = issuingDate;
        this.punishmentDuration = punishmentDuration;

        this.id = UUID.randomUUID();

        savePunishment();
    }

    public void savePunishment() {
        Document document = Document.parse(toJson());
        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreMongoDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", this.id), document, new ReplaceOptions().upsert(true)));
    }

    public String toJson() {
        return CorePlugin.GSON.toJson(this);
    }

    public static Punishment getById(String id) {
        return CorePlugin.GSON.fromJson(Objects.requireNonNull(CorePlugin.getInstance().getCoreMongoDatabase().getPunishmentCollection().find().filter(Filters.eq("_id", id)).first()).toJson(), Punishment.class);
    }

    public static ArrayList<Punishment> getAllPunishments() {
        return CorePlugin.getInstance().getPunishmentManager().getPunishments();
    }
}
