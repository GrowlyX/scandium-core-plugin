package vip.potclub.core.manager;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.punishment.Punishment;

import java.util.ArrayList;

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

    public void handlePunishment(Punishment punishment) {

    }
}
