package vip.potclub.core.manager;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.bson.Document;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.rank.Rank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class RankManager {

    public RankManager() {
        this.createDefaultRanks();

        CorePlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
            for (Document document : CorePlugin.getInstance().getCoreMongoDatabase().getRanksCollection().find()) {
                Rank.getRegisteredRanks().add(CorePlugin.GSON.fromJson(document.toJson(), Rank.class));
            }
        });
    }

    public void saveAllRanks() {
        Rank.getRegisteredRanks().forEach(Rank::serializeRank);
    }

    private void createDefaultRanks() {
        if (CorePlugin.getInstance().getCoreMongoDatabase().getRanksCollection().countDocuments() <= 0L) {
            if (CorePlugin.getInstance().getCoreMongoDatabase().getRanksCollection().find(Filters.eq("_id", "default")).first() == null) {
                List<String> permissions = Arrays.asList("practice.default", "core.default", "test.default");
                Document document = new Document("_id", "default");

                document.put("name", "Default");
                document.put("prefix", "§7");
                document.put("color", "§7");
                document.put("weight", 1);

                document.put("isStaff", false);
                document.put("isNormal", true);
                document.put("isDonator", false);
                document.put("isDeveloper", false);

                document.put("permissions", permissions);

                CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreMongoDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", "default"), document, new ReplaceOptions().upsert(true)));
            }
            if (CorePlugin.getInstance().getCoreMongoDatabase().getRanksCollection().find(Filters.eq("_id", "owner")).first() == null) {
                List<String> permissions = Arrays.asList("practice.owner", "core.owner", "test.owner");
                Document document = new Document("_id", "owner");

                document.put("name", "Owner");
                document.put("prefix", "§4[Owner] ");
                document.put("color", "§4");
                document.put("weight", 1000);

                document.put("isStaff", true);
                document.put("isNormal", false);
                document.put("isDonator", true);
                document.put("isDeveloper", true);

                document.put("permissions", permissions);

                CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreMongoDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", "owner"), document, new ReplaceOptions().upsert(true)));
            }
            if (CorePlugin.getInstance().getCoreMongoDatabase().getRanksCollection().find(Filters.eq("_id", "manager")).first() == null) {
                List<String> permissions = Arrays.asList("practice.manager", "core.manager", "test.manager");
                Document document = new Document("_id", "manager");

                document.put("name", "Manager");
                document.put("prefix", "§c[Manager] ");
                document.put("color", "§c");
                document.put("weight", 900);

                document.put("isStaff", true);
                document.put("isNormal", false);
                document.put("isDonator", true);
                document.put("isDeveloper", true);

                document.put("permissions", permissions);

                CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreMongoDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", "manager"), document, new ReplaceOptions().upsert(true)));
            }
        }
    }
}
