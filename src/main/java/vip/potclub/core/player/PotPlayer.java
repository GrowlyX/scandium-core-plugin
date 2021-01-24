package vip.potclub.core.player;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.punishment.Punishment;
import vip.potclub.core.player.punishment.PunishmentType;

import java.beans.ConstructorProperties;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
public class PotPlayer {

    private static Map<UUID, PotPlayer> profilePlayers = new HashMap<>();

    private List<Punishment> punishments = new ArrayList<>();

    private UUID uuid;
    private Player player;
    private String name;

    public boolean canSeeStaffMessages = true;
    public boolean canSeeGlobalChat = true;
    public boolean canReceiveDms = true;
    public boolean canReceiveDmsSounds = true;
    public boolean canSeeTips = true;

    public boolean canReport = true;
    public boolean canRequest = true;

    public boolean currentlyMuted;
    public boolean currentlyBanned;

    @ConstructorProperties({"uuid"})
    public PotPlayer(UUID uuid) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
        this.name = player.getName();

        loadPlayerData();

        profilePlayers.put(uuid, this);
    }

    public void saveWithoutRemove() {
        Document document = new Document("_id", this.uuid);

        document.put("name", name);
        document.put("canSeeStaffMessages", canSeeStaffMessages);
        document.put("canSeeTips", canSeeTips);
        document.put("canReceiveDms", canReceiveDms);
        document.put("canSeeGlobalChat", canSeeGlobalChat);
        document.put("canReceiveDmsSounds", canReceiveDmsSounds);

        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", uuid), document, new ReplaceOptions().upsert(true)));
    }

    public void savePlayerData() {
        Document document = new Document("_id", this.uuid);

        document.put("name", name);
        document.put("canSeeStaffMessages", canSeeStaffMessages);
        document.put("canSeeTips", canSeeTips);
        document.put("canReceiveDms", canReceiveDms);
        document.put("canSeeGlobalChat", canSeeGlobalChat);
        document.put("canReceiveDmsSounds", canReceiveDmsSounds);

        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", uuid), document, new ReplaceOptions().upsert(true)));

        profilePlayers.remove(uuid);
    }

    public void loadPlayerData() {
        Document document = CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("_id", this.uuid)).first();
        if (document == null) return;

        this.name = document.getString("name");

        if (document.getBoolean("canSeeStaffMessages") != null) {
            this.canSeeStaffMessages = document.getBoolean("canSeeStaffMessages");
        }
        if (document.getBoolean("canSeeTips") != null) {
            this.canSeeTips = document.getBoolean("canSeeTips");
        }
        if (document.getBoolean("canReceiveDms") != null) {
            this.canReceiveDms = document.getBoolean("canReceiveDms");
        }
        if (document.getBoolean("canSeeGlobalChat") != null) {
            this.canSeeGlobalChat = document.getBoolean("canSeeGlobalChat");
        }
        if (document.getBoolean("canReceiveDmsSounds") != null) {
            this.canReceiveDmsSounds = document.getBoolean("canReceiveDmsSounds");
        }

        CorePlugin.getInstance().getPunishmentManager().getPunishments().forEach(punishment -> {
            if (punishment.getTarget().equals(this.uuid)) {
                this.punishments.add(punishment);
            }
        });

        this.currentlyMuted = this.isMuted();
        this.currentlyBanned = this.isBanned();
    }

    public boolean isMuted() {
        MongoCollection<Document> mongoCollection = CorePlugin.getInstance().getCoreDatabase().getPunishmentCollection();
        AtomicBoolean yes = new AtomicBoolean(false);
        CorePlugin.getInstance().getMongoThread().execute(() -> {
            for (Document document : mongoCollection.find()) {
                if (document.get("punishmentType").equals("MUTE")) {
                    if (document.get("target").equals(this.uuid.toString())) {
                        yes.set(document.get("active").equals(true));
                    }
                }
            }
        });

        return yes.get();
    }

    public boolean isBanned() {
        MongoCollection<Document> mongoCollection = CorePlugin.getInstance().getCoreDatabase().getPunishmentCollection();
        AtomicBoolean yes = new AtomicBoolean(false);
        CorePlugin.getInstance().getMongoThread().execute(() -> {
            for (Document document : mongoCollection.find()) {
                if (document.get("punishmentType").equals("BAN")) {
                    if (document.get("target").equals(this.uuid.toString())) {
                        yes.set(document.get("active").equals(true));
                    }
                }
            }
        });

        return yes.get();
    }

    public void unMutePlayer() {
        MongoCollection<Document> mongoCollection = CorePlugin.getInstance().getCoreDatabase().getPunishmentCollection();
        CorePlugin.getInstance().getMongoThread().execute(() -> {
            for (Document document : mongoCollection.find()) {
                if (document.get("punishmentType").equals("MUTE")) {
                    if (document.get("target").equals(this.uuid.toString())) {
                        if (!document.get("active").equals(false)) {
                            this.currentlyMuted = false;
                            Punishment punishment = CorePlugin.GSON.fromJson(document.toJson(), Punishment.class);
                            punishment.setActive(false);
                            punishment.setRemoved(true);
                            punishment.savePunishment();
                        }
                    }
                }
            }
        });
    }

    public void unBanPlayer() {
        MongoCollection<Document> mongoCollection = CorePlugin.getInstance().getCoreDatabase().getPunishmentCollection();
        CorePlugin.getInstance().getMongoThread().execute(() -> {
            for (Document document : mongoCollection.find()) {
                if (document.get("punishmentType").equals("BAN")) {
                    if (document.get("target").equals(this.uuid.toString())) {
                        if (!document.get("active").equals(false)) {
                            this.currentlyBanned = false;
                            Punishment punishment = CorePlugin.GSON.fromJson(document.toJson(), Punishment.class);
                            punishment.setActive(false);
                            punishment.setRemoved(true);
                            punishment.savePunishment();
                        }
                    }
                }
            }
        });
    }

    public static PotPlayer getPlayer(Player player) {
        return profilePlayers.get(player.getUniqueId());
    }

    public static PotPlayer getPlayer(UUID uuid) {
        return profilePlayers.get(uuid);
    }

    public static PotPlayer getPlayer(String name) {
        return profilePlayers.get(Bukkit.getPlayer(name).getUniqueId());
    }
}
