package vip.potclub.core.manager;

import lombok.Getter;
import org.bson.Document;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.ranks.Rank;

import java.text.SimpleDateFormat;
import java.util.UUID;

@Getter
public class RankManager {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

    public RankManager() {
        this.loadRanks();
    }

    public void loadRanks() {
        CorePlugin.getInstance().getMongoThread().execute(() -> {
            for (Document document : CorePlugin.getInstance().getCoreDatabase().getRankCollection().find()) {
                new Rank(
                        UUID.fromString(document.getString("uuid")),
                        document.getList("inheritance", UUID.class),
                        document.getList("permissions", String.class),
                        document.getString("name"), document.getString("prefix"),
                        document.getString("color"), document.getString("suffix"),
                        document.getBoolean("defaultRank"),
                        document.getInteger("weight")
                );
            }
        });
    }

    public void saveRanks() {
        Rank.getRanks().forEach(Rank::saveRank);
    }
}
