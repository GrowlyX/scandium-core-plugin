package com.solexgames.core.player.ranks;

import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.solexgames.core.CorePlugin;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author GrowlyX
 * @since March 2021
 */

@Getter
@Setter
public class Rank {

    private List<UUID> inheritance;
    private List<String> permissions;

    @SerializedName("_id")
    private UUID uuid;

    private String name;
    private String prefix;
    private String suffix;
    private String teamLetter;

    private String color;

    private boolean defaultRank;

    private boolean italic = false;
    private boolean hidden = false;
    private boolean purchasable = false;

    private int weight;

    public Rank(UUID uuid, List<UUID> inheritance, List<String> permissions, String name, String prefix, String color, String suffix, boolean defaultRank, int weight) {
        this.uuid = uuid;
        this.inheritance = inheritance;
        this.permissions = permissions;
        this.name = name;
        this.prefix = prefix;
        this.color = color;
        this.suffix = suffix;
        this.defaultRank = defaultRank;
        this.weight = weight;

        CorePlugin.getInstance().getRankManager().getRanks().add(this);
    }

    public Rank(String name) {
        this.uuid = UUID.randomUUID();
        this.inheritance = new ArrayList<>();
        this.permissions = new ArrayList<>();
        this.name = name;
        this.prefix = "&7";
        this.color = "&7";
        this.suffix = "";
        this.defaultRank = true;
        this.weight = 0;

        CorePlugin.getInstance().getRankManager().getRanks().add(this);
    }

    public Rank(UUID uuid, String name) {
        this.uuid = uuid;
        this.inheritance = new ArrayList<>();
        this.permissions = new ArrayList<>();
        this.name = name;
        this.prefix = "&7";
        this.color = "&7";
        this.suffix = "";
        this.defaultRank = true;
        this.weight = 0;

        CorePlugin.getInstance().getRankManager().getRanks().add(this);
    }

    public boolean isItalic() {
        return this.italic;
    }

    public void saveRank() {
        CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getRankCollection().replaceOne(Filters.eq("_id", this.uuid), this.getDocument(), new ReplaceOptions().upsert(true)));
    }

    public void saveMainThread() {
        CorePlugin.getInstance().getCoreDatabase().getRankCollection().replaceOne(Filters.eq("_id", this.uuid), this.getDocument(), new ReplaceOptions().upsert(true));
    }

    public Document getDocument() {
        Document document = new Document("_id", this.uuid);

        document.put("uuid", this.uuid.toString());
        document.put("inheritance", this.inheritance);
        document.put("permissions", this.permissions);
        document.put("name", this.name);
        document.put("prefix", this.prefix);
        document.put("color", this.color);
        document.put("suffix", this.suffix);
        document.put("defaultRank", this.defaultRank);
        document.put("weight", this.weight);
        document.put("hidden", this.hidden);
        document.put("italic", this.italic);

        document.put("purchasable", this.purchasable);

        return document;
    }

    public String getItalic() {
        return this.italic ? ChatColor.ITALIC.toString() : "";
    }

    public static Rank getDefault() {
        return CorePlugin.getInstance().getRankManager().getRanks().stream()
                .filter(Rank::isDefaultRank)
                .findFirst().orElseGet(() -> new Rank("Default"));
    }

    public static Rank getByName(String name) {
        return CorePlugin.getInstance().getRankManager().getRanks().stream()
                .filter(rank -> rank.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public static Rank getByUuid(UUID uuid) {
        return CorePlugin.getInstance().getRankManager().getRanks().stream()
                .filter(rank -> rank.getUuid().equals(uuid))
                .findFirst().orElse(null);
    }
}
