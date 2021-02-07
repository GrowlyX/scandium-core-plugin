package vip.potclub.core.manager;

import com.mongodb.client.model.Filters;
import org.bson.Document;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.ranks.Rank;

import java.util.*;
import java.util.stream.Collectors;

public class RankManager {

    public RankManager() {
        this.createDefaultRanks();
        this.loadRanks();
        CorePlugin.getInstance().getLogger().info("[Ranks] Loaded all ranks.");
    }

    public void loadRanks() {
        CorePlugin.getInstance().getMongoThread().execute(() -> {
            for (Document document : CorePlugin.getInstance().getCoreDatabase().getRankCollection().find()) {
                Rank rank = new Rank(
                        document.get("_id", UUID.class),
                        (ArrayList<UUID>) document.get("inheritance"),
                        (ArrayList<String>) document.get("permissions"),
                        document.getString("name"),
                        document.getString("prefix"),
                        document.getString("color"),
                        document.getString("suffix"),
                        document.getBoolean("defaultRank"),
                        document.getInteger("weight")
                );

                Rank.getRanks().add(rank);
            }
        });
    }

    private void createDefaultRanks() {
        if (CorePlugin.getInstance().getCoreDatabase().getRankCollection().find(Filters.eq("name", "Default")).first() == null) {
            List<String> permissions = Collections.singletonList("scandium.default");
            Document defaultRank = new Document("_id", UUID.randomUUID());

            defaultRank.put("uuid", UUID.randomUUID().toString());
            defaultRank.put("inheritance", new ArrayList<UUID>());
            defaultRank.put("permissions", permissions);
            defaultRank.put("name", "Default");
            defaultRank.put("prefix", "&7");
            defaultRank.put("color", "&7");
            defaultRank.put("suffix", "&7");
            defaultRank.put("defaultRank", true);
            defaultRank.put("weight", 0);

            CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getRankCollection().insertOne(defaultRank));
        }
    }

    public void saveRanks() {
        this.getSortedRanks().forEach(Rank::saveMainThread);
    }

    public List<Rank> getSortedRanks() {
        return Rank.getRanks().stream().sorted(Comparator.comparingInt(Rank::getWeight)).collect(Collectors.toList());
    }
}
