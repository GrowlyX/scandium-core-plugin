package com.solexgames.core.player.ranks;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.solexgames.core.CorePlugin;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Setter
public class Rank {

    @Getter
    @Setter
    private static List<Rank> ranks = new ArrayList<>();

    public final List<UUID> inheritance;
    public final List<String> permissions;

    private UUID uuid;

    private String name;
    private String prefix;
    private String suffix;
    private String teamLetter;

    private String color;

    private boolean defaultRank;
    private boolean hidden = false;

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

        ranks.add(this);
    }

    public void saveRank() {
        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getRankCollection().replaceOne(Filters.eq("uuid", this.uuid.toString()), this.getDocument(), new ReplaceOptions().upsert(true)));
    }

    public void saveMainThread() {
        CorePlugin.getInstance().getCoreDatabase().getRankCollection().replaceOne(Filters.eq("uuid", this.uuid.toString()), this.getDocument(), new ReplaceOptions().upsert(true));
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

        return document;
    }

    public static Rank getDefault() {
        AtomicReference<Rank> rankAtomicReference = new AtomicReference<>();

        Rank.getRanks().forEach(rank -> {
            if (rank.isDefaultRank()) {
                rankAtomicReference.set(rank);
            }
        });

        return rankAtomicReference.get();
    }

    public static Rank getByName(String name) {
        return getRanks().stream().filter(rank -> rank.getName().equals(name)).findFirst().orElse(null);
    }

    public static Rank getByUuid(UUID uuid) {
        return getRanks().stream().filter(rank -> rank.getUuid().equals(uuid)).findFirst().orElse(null);
    }
}
