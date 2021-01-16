package vip.potclub.core.rank;

import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.manager.RankManager;
import vip.potclub.core.player.PotPlayer;

import java.beans.ConstructorProperties;
import java.util.*;

@Getter
@Setter
public class Rank {

    @Getter
    private static List<Rank> registeredRanks = new ArrayList<>();

    private List<String> inherits = new ArrayList<>();
    private List<String> permissions = new ArrayList<>();

    private String id;

    private int weight;

    private String name;
    private String prefix;
    private String color;

    private boolean staff = false;
    private boolean developer = false;
    private boolean donator = false;
    private boolean normal = true;

    @ConstructorProperties("id")
    public Rank(String id, String name, String prefix, String color, int weight) {
        this.id = id;
        this.name = name;
        this.prefix = prefix.replace("&", "§");
        this.color = color.replace("&", "§");
        this.weight = weight;

        deserializeRank();

        registeredRanks.add(this);
    }

    public void serializeRank() {
        Document document = new Document("_id", this.id);

        document.put("name", this.name);
        document.put("prefix", this.prefix);
        document.put("color", this.color);
        document.put("weight", this.weight);

        document.put("isStaff", this.staff);
        document.put("isNormal", this.normal);
        document.put("isDonator", this.donator);
        document.put("isDeveloper", this.developer);

        document.put("permissions", this.permissions);
        document.put("inherits", this.inherits);

        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreMongoDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", this.id), document, new ReplaceOptions().upsert(true)));
        registeredRanks.remove(this);
    }

    public void deserializeRank() {
        Document document = CorePlugin.getInstance().getCoreMongoDatabase().getPlayerCollection().find(Filters.eq("_id", this.id)).first();
        if (document == null) return;

        if (document.getString("name") != null) {
            this.name = document.getString("name");
        }
        if (document.getString("prefix") != null) {
            this.prefix = document.getString("prefix");
        }
        if (document.getString("color") != null) {
            this.color = document.getString("color");
        }
        if (document.getInteger("weight") != null) {
            this.weight = document.getInteger("weight");
        }
        if (document.getBoolean("isStaff") != null) {
            this.staff = document.getBoolean("isStaff");
        }
        if (document.getBoolean("isNormal") != null) {
            this.normal = document.getBoolean("isNormal");
        }
        if (document.getBoolean("isDonator") != null) {
            this.donator = document.getBoolean("isDonator");
        }
        if (document.getBoolean("isDeveloper") != null) {
            this.developer = document.getBoolean("isDeveloper");
        }
        if (document.get("inherits") != null) {
            this.inherits = (List<String>) document.get("inherits");
        }
        if (document.get("permissions") != null) {
            this.permissions = (List<String>) document.get("permissions");
        }
    }

    public List<String> getInheritances() {
        List<String> inheritances = new ArrayList<>(this.inherits);
        this.inherits.forEach(inheritedRankId -> inheritances.addAll(getById(inheritedRankId).getInheritances()));
        return inheritances;
    }

    public List<String> getAllPermissions() {
        List<String> allPermissions = new ArrayList<>(permissions);
        this.getInheritances().forEach(inheritance -> allPermissions.addAll(getById(inheritance).getPermissions()));
        return allPermissions;
    }

    public boolean hasPermission(String requiredPermission) {
        return this.permissions.stream().anyMatch((permission) -> permission.equalsIgnoreCase(requiredPermission));
    }

    public int compareTo(Rank rank) {
        return Integer.compare(this.weight, rank.weight);
    }

    public static Rank getById(String id) {
        return getRegisteredRanks().stream().filter(rank -> rank.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public static Rank getDefaultRank() {
        return getById("default");
    }
}
