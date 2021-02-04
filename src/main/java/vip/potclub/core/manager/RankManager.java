package vip.potclub.core.manager;

import com.mongodb.client.model.Filters;
import lombok.Getter;
import org.bson.Document;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.ranks.Rank;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class RankManager {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

    public RankManager() {
        this.createDefaultRanks();
        this.loadRanks();
        CorePlugin.getInstance().getLogger().info("[Ranks] Loaded all ranks.");
    }

    public void loadRanks() {
        CorePlugin.getInstance().getMongoThread().execute(() -> {
            for (Document document : CorePlugin.getInstance().getCoreDatabase().getRankCollection().find()) {
                new Rank(
                        UUID.fromString(document.getString("uuid")),
                        document.getList("inheritance", UUID.class),
                        document.getList("permissions", String.class),
                        document.getString("name"),
                        document.getString("prefix"),
                        document.getString("color"),
                        document.getString("suffix"),
                        document.getBoolean("defaultRank"),
                        document.getInteger("weight")
                );
            }
        });
    }

    private void createDefaultRanks() {
        if (CorePlugin.getInstance().getCoreDatabase().getRankCollection().find(Filters.eq("name", "Default")).first() == null) {
            List<String> permissions = Collections.singletonList("scandium.default");
            Document defaultRank = new Document("_id", UUID.randomUUID());

            defaultRank.put("uuid", UUID.randomUUID());
            defaultRank.put("inheritance", null);
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
        Rank.getRanks().forEach(Rank::saveRank);
    }
}
