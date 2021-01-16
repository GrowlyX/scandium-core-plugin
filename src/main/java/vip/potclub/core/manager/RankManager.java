package vip.potclub.core.manager;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import org.bson.Document;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.rank.Rank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public class RankManager {

    private final List<Rank> ranks = new ArrayList<>();

    public RankManager() {
        this.createDefaultRank();

        CorePlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
            for (Document rankDocument : CorePlugin.getInstance().getCoreMongoDatabase().getRanksCollection().find()) {
                this.ranks.add(CorePlugin.GSON.fromJson(rankDocument.toJson(), Rank.class));
            }
        });
    }

    private void createDefaultRank() {
        if (CorePlugin.getInstance().getCoreMongoDatabase().getRanksCollection().countDocuments() <= 0L) {
            if (CorePlugin.getInstance().getCoreMongoDatabase().getRanksCollection().find(Filters.eq("id", "default")).first() == null) {
                List<String> permissions = Arrays.asList("practice.default", "core.default", "test.default");
                Document defaultRank = new Document("id", "default");

                defaultRank.append("weight", 1);
                defaultRank.append("permissions", permissions);
                defaultRank.append("name", "Default");
                defaultRank.append("prefix", "§7");
                defaultRank.append("color", "§7");
                CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreMongoDatabase().getRanksCollection().insertOne(defaultRank));
            }
            if (CorePlugin.getInstance().getCoreMongoDatabase().getRanksCollection().find(Filters.eq("id", "owner")).first() == null) {
                List<String> permissions = Arrays.asList("practice.staff", "core.staff", "test.staff");
                Document defaultRank = new Document("id", "owner");

                defaultRank.append("weight", 1000);
                defaultRank.append("permissions", permissions);
                defaultRank.append("name", "Owner");
                defaultRank.append("prefix", "§4[Owner] §4");
                defaultRank.append("color", "§4");
                CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreMongoDatabase().getRanksCollection().insertOne(defaultRank));
            }
            if (CorePlugin.getInstance().getCoreMongoDatabase().getRanksCollection().find(Filters.eq("id", "manager")).first() == null) {
                List<String> permissions = Arrays.asList("practice.staff", "core.staff", "test.staff");
                Document defaultRank = new Document("id", "manager");

                defaultRank.append("weight", 900);
                defaultRank.append("permissions", permissions);
                defaultRank.append("name", "Manager");
                defaultRank.append("prefix", "§c[Manager] §c");
                defaultRank.append("color", "§c");
                CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreMongoDatabase().getRanksCollection().insertOne(defaultRank));
            }
        }
    }

    public static Rank getById(String id) {
        return CorePlugin.getInstance().getRankManager().getRanks().stream().filter(rank -> rank.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public static Rank getDefaultRank() {
        return getById("default");
    }
}
