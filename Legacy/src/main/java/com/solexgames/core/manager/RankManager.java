package com.solexgames.core.manager;

import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.ranks.Rank;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class RankManager {

    public RankManager() {
        this.createDefaultRanks();
        this.loadRanks();
    }

    public void loadRanks() {
        CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getRankCollection().find().forEach((Block<? super Document>) document -> {
            if (Rank.getByName(document.getString("name")) == null) {
                Rank rank = new Rank(
                        UUID.fromString(document.getString("uuid")),
                        (ArrayList<UUID>) document.get("inheritance"),
                        (ArrayList<String>) document.get("permissions"),
                        document.getString("name"),
                        document.getString("prefix"),
                        document.getString("color"),
                        document.getString("suffix"),
                        document.getBoolean("defaultRank"),
                        document.getInteger("weight")
                );

                if (document.getBoolean("hidden") != null) {
                    rank.setHidden(document.getBoolean("hidden"));
                }
                if (document.getBoolean("purchasable") != null) {
                    rank.setPurchasable(document.getBoolean("purchasable"));
                }
                if (document.getBoolean("italic") != null) {
                    rank.setItalic(document.getBoolean("italic"));
                }
            }
        }));
    }

    private void createDefaultRanks() {
        if (CorePlugin.getInstance().getCoreDatabase().getRankCollection().find(Filters.eq("name", "Default")).first() == null) {
            List<String> permissions = Collections.singletonList("scandium.default");
            UUID uuid = UUID.randomUUID();
            Document defaultRank = new Document("_id", uuid);

            defaultRank.put("uuid", uuid.toString());
            defaultRank.put("inheritance", new ArrayList<UUID>());
            defaultRank.put("permissions", permissions);
            defaultRank.put("name", "Default");
            defaultRank.put("prefix", "&7");
            defaultRank.put("color", "&7");
            defaultRank.put("suffix", "&7");
            defaultRank.put("defaultRank", true);
            defaultRank.put("weight", 0);
            defaultRank.put("hidden", false);

            defaultRank.put("purchasable", false);

            CorePlugin.getInstance().getCoreDatabase().getRankCollection().insertOne(defaultRank);
        }
    }

    public void saveRanks() {
        this.getSortedRanks().forEach(Rank::saveMainThread);
    }

    public List<Rank> getSortedRanks() {
        return Rank.getRanks().stream()
                .sorted(Comparator.comparingInt(Rank::getWeight))
                .collect(Collectors.toList());
    }
}
