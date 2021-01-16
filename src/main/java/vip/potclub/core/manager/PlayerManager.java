package vip.potclub.core.manager;

import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ChatChannel;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Map<UUID, PotPlayer> players = new HashMap<>();

    public PotPlayer addPlayer(UUID uuid, InetAddress ipAddress) {
        PotPlayer potPlayer = new PotPlayer(uuid, ipAddress);
        potPlayer.setUuid(uuid);
        this.players.put(uuid, potPlayer);
        return potPlayer;
    }

    public PotPlayer getPlayer(String name) {
        UUID uuid = Bukkit.getPlayer(name) == null ? Bukkit.getOfflinePlayer(name).getUniqueId() : Bukkit.getPlayer(name).getUniqueId();
        return this.getPlayer(uuid);
    }

    public PotPlayer getPlayer(UUID uuid) {
        return this.getPlayer(uuid, false);
    }

    public PotPlayer getPlayer(UUID uuid, boolean load) {
        PotPlayer potplayer = null;
        if (this.players.containsKey(uuid)) {
            potplayer = this.players.get(uuid);
        } else if (load) {
            potplayer = new PotPlayer(uuid, null);
            potplayer.setUuid(uuid);
        }

        return potplayer;
    }

    public void removePlayer(UUID player) {
        this.players.remove(player);
    }

    public Document getDocumentFromName(String name) {
        Document basicQuery = new Document();
        basicQuery.append("name", (new Document("$regex", name)).append("$options", "i"));
        return CorePlugin.getInstance().getCoreMongoDatabase().getPlayerCollection().find(basicQuery).first();
    }

    public UUID getUuidFromName(String name) {
        Document document = this.getDocumentFromName(name);
        return document != null ? UUID.fromString(document.getString("uuid")) : null;
    }

    public String getNameFromUuid(UUID uuid) {
        Document document = CorePlugin.getInstance().getCoreMongoDatabase().getPlayerCollection().find(Filters.eq("_id", uuid.toString())).first();
        return document != null ? document.getString("name") : null;
    }

    public String formatChatChannel(ChatChannel chatChannel, String player, String message, String fromServer) {
        return Color.translate(chatChannel.getPrefix() + "&7[" + fromServer + "] " + player + "&f: &b" + message);
    }

    public String formatBroadcast(String message) {
        return Color.translate("&8[&4Alert&8] &f" + message);
    }
}
