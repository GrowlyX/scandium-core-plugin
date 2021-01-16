package vip.potclub.core.player;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ChatChannel;
import vip.potclub.core.manager.RankManager;
import vip.potclub.core.rank.Rank;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.DatabaseUtil;
import vip.potclub.core.util.HashingUtil;
import vip.potclub.core.util.RedisUtil;

import java.beans.ConstructorProperties;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Getter
@Setter
public class PotPlayer {

    private List<String> ownedPermissions = new ArrayList<>();
    private final InetAddress ipAddress;

    public UUID uuid;
    public String dbIpAddress;
    public String name;
    public Player player;

    public boolean canSeeStaffMessages;

    private PermissionAttachment permissions;
    private Rank rank = RankManager.getDefaultRank();

    @ConstructorProperties({"uuid", "inetAddress"})
    public PotPlayer(UUID uuid, InetAddress inetAddress) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
        this.name = player.getName();
        this.ipAddress = inetAddress;

        loadPlayerData();
    }

    public void savePlayerData() {
        Document document = new Document("_id", this.uuid);

        document.put("name", this.name);
        document.put("rank", this.rank.getId());
        document.put("ipAddress", this.dbIpAddress);
        document.put("permissions", this.ownedPermissions);
        document.put("canSeeStaffMessages", this.canSeeStaffMessages);

        DatabaseUtil.saveDocument(document, this.uuid);
        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onDisconnect(player)));
        CorePlugin.getInstance().getPlayerManager().removePlayer(uuid);
    }

    public void loadPlayerData() {
        Document document = CorePlugin.getInstance().getCoreMongoDatabase().getPlayerCollection().find(Filters.eq("_id", this.uuid)).first();
        if (document == null) return;

        this.name = document.getString("name");
        this.rank = RankManager.getById(document.getString("rank")) != null ? RankManager.getById(document.getString("rank")) : RankManager.getDefaultRank();
        this.dbIpAddress = document.getString("ipAddress");

        if (document.get("permissions") != null) {
            this.ownedPermissions.addAll((Collection<? extends String>) document.get("permissions"));
        }

        this.canSeeStaffMessages = document.getBoolean("canSeeStaffMessages");
        this.permissions = player.addAttachment(CorePlugin.getInstance());

        onConnect();
        CorePlugin.getInstance().getPlayerManager().addPlayer(uuid, ipAddress);
    }

    public void onConnect() {
        this.updateDisplayName();
        this.updatePermissions();
        this.updateTabList(this.rank);

        Bukkit.getScheduler().runTaskLaterAsynchronously(CorePlugin.getInstance(), () -> {
            if (player.getAddress() != null) {
                this.dbIpAddress = HashingUtil.getSaltedMD5(player.getAddress().getAddress().getHostAddress());
                CorePlugin.getInstance().getCoreMongoDatabase().getPlayerCollection().updateOne(
                        Filters.eq("uuid", player.getUniqueId().toString()),
                        Filters.and(Updates.set("ipAddress", this.dbIpAddress)));
            }
        }, 1L);

        CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onConnect(player)));
    }

    public void updateDisplayName() {
        Player player = this.getPlayer();
        String color = this.getRank().getColor();
        if (!player.getDisplayName().equals(color + player.getName())) {
            player.setDisplayName(color + player.getName());
        }
    }

    public void updatePermissions() {
        this.permissions.getPermissions().forEach((permission, permissionBoolean) -> {
            this.permissions.unsetPermission(permission);
        });
        Stream.concat(this.ownedPermissions.stream(), this.rank.getPermissions().stream()).distinct().sorted().forEach((permission) -> {
            this.permissions.setPermission(permission, true);
        });
    }

    public void updateTabList(Rank rank) {
        this.getPlayer().setPlayerListName(Color.translate(rank.getColor() + this.getPlayer().getDisplayName() + ChatColor.RESET));
    }
}
