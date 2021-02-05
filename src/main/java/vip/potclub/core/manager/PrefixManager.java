package vip.potclub.core.manager;

import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.prefixes.Prefix;

public class PrefixManager {

    public PrefixManager() {
        this.createDefaultPrefixes();
        this.loadPrefixes();
        CorePlugin.getInstance().getLogger().info("[Prefixes] Loaded all prefixes.");
    }

    private void createDefaultPrefixes() {
        if (CorePlugin.getInstance().getCoreDatabase().getPrefixCollection().find(Filters.eq("name", "Verified")).first() == null) new Prefix("Verified", "&2✔");
        if (CorePlugin.getInstance().getCoreDatabase().getPrefixCollection().find(Filters.eq("name", "Liked")).first() == null) new Prefix("Liked", "&b✔");
    }

    public void loadPrefixes() {
        CorePlugin.getInstance().getMongoThread().execute(() -> {
            for (Document document : CorePlugin.getInstance().getCoreDatabase().getPrefixCollection().find()) {
                new Prefix(document.getString("_id"), document.getString("name"), document.getString("displayName"), document.getString("prefix"));
            }
        });
    }

    public void savePrefixes() {
        Prefix.getPrefixes().forEach(Prefix::savePrefixMainThread);
    }
}
