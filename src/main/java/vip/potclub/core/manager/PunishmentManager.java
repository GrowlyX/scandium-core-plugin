package vip.potclub.core.manager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.punishment.Punishment;
import vip.potclub.core.util.Color;

import java.util.ArrayList;

@Getter
@Setter
public class PunishmentManager {

    private final ArrayList<Punishment> punishments = new ArrayList<>();

    public PunishmentManager() {

        CorePlugin.getInstance().getMongoThread().execute(() -> {
            for (Document punishmentDocument : CorePlugin.getInstance().getCoreMongoDatabase().getPunishmentCollection().find()) {
                this.punishments.add(CorePlugin.GSON.fromJson(punishmentDocument.toJson(), Punishment.class));
            }
        });

        CorePlugin.getInstance().getLogger().info("[Punishments] Loaded all punishments.");
    }

    public void savePunishments() {
        MongoCollection<Document> mongoCollection = CorePlugin.getInstance().getCoreMongoDatabase().getPunishmentCollection();
        for (Document document : mongoCollection.find()) {
            mongoCollection.deleteOne(document);
        }
        this.punishments.forEach(Punishment::savePunishment);
    }
}
