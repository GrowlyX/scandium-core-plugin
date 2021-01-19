package vip.potclub.core.player;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.punishment.Punishment;

import java.beans.ConstructorProperties;
import java.util.*;

@Getter
@Setter
public class PotPlayer {

    private static Map<UUID, PotPlayer> profilePlayers = new HashMap<>();

    private List<UUID> punishments = new ArrayList<>();

    private UUID uuid;
    private Player player;
    private String name;

    public boolean canSeeStaffMessages = true;
    public boolean canSeeTips = true;
    public boolean canReport = true;
    public boolean canRequest = true;

    @ConstructorProperties({"uuid"})
    public PotPlayer(UUID uuid) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
        this.name = player.getName();

        loadPlayerData();

        profilePlayers.put(uuid, this);
    }

    public void savePlayerData() {
        Document document = new Document("_id", this.uuid);

        document.put("name", name);
        document.put("canSeeStaffMessages", canSeeStaffMessages);
        document.put("canSeeTips", canSeeTips);

        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreMongoDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", uuid), document, new ReplaceOptions().upsert(true)));

        profilePlayers.remove(uuid);
    }

    public void loadPlayerData() {
        Document document = CorePlugin.getInstance().getCoreMongoDatabase().getPlayerCollection().find(Filters.eq("_id", this.uuid)).first();
        if (document == null) return;

        this.name = document.getString("name");
        if (document.getBoolean("canSeeStaffMessages") != null) {
            this.canSeeStaffMessages = document.getBoolean("canSeeStaffMessages");
        }
        if (document.getBoolean("canSeeTips") != null) {
            this.canSeeTips = document.getBoolean("canSeeTips");
        }
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
