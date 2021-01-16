package vip.potclub.core.player;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.manager.RankManager;
import vip.potclub.core.rank.Rank;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;

import java.beans.ConstructorProperties;
import java.util.*;

@Getter
@Setter
public class PotPlayer {

    private static Map<UUID, PotPlayer> profilePlayers = new HashMap<>();

    private List<String> ownedPermissions = new ArrayList<>();

    private UUID uuid;
    private Player player;
    private String name;

    public boolean canSeeStaffMessages;

    private PermissionAttachment permissions;
    private Rank rank = RankManager.getDefaultRank();

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
        document.put("rank", rank.getId());

        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreMongoDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", uuid), document, new ReplaceOptions().upsert(true)));
        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onDisconnect(player)));

        profilePlayers.remove(uuid);
    }

    public void loadPlayerData() {
        Document document = CorePlugin.getInstance().getCoreMongoDatabase().getPlayerCollection().find(Filters.eq("_id", this.uuid)).first();
        if (document == null) return;

        this.name = document.getString("name");
        this.rank = RankManager.getById(document.getString("rank")) != null ? RankManager.getById(document.getString("rank")) : RankManager.getDefaultRank();

        if (!this.getPlayer().getDisplayName().equals(this.getRank().getColor() + this.getPlayer().getName())) {
            this.getPlayer().setDisplayName(this.getRank().getColor() + this.getPlayer().getName());
        }

        this.rank.getPermissions().forEach((permission) -> this.permissions.setPermission(permission, true));
        this.getPlayer().setPlayerListName(Color.translate(rank.getColor() + this.getPlayer().getDisplayName() + ChatColor.RESET));

        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onConnect(player)));
    }

    public void updateServerPlayer() {
        if (!this.getPlayer().getDisplayName().equals(this.getRank().getColor() + this.getPlayer().getName())) {
            this.getPlayer().setDisplayName(this.getRank().getColor() + this.getPlayer().getName());
        }

        this.rank.getPermissions().forEach((permission) -> this.permissions.setPermission(permission, true));
        this.getPlayer().setPlayerListName(Color.translate(rank.getColor() + this.getPlayer().getDisplayName() + ChatColor.RESET));
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
