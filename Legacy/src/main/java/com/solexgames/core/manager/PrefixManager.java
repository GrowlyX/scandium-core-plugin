package com.solexgames.core.manager;

import com.mongodb.client.model.Filters;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.prefixes.Prefix;
import org.bson.Document;

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
                if (Prefix.getByName(document.getString("name")) == null) {
                    new Prefix(document.getString("_id"), document.getString("name"), document.getString("displayName"), document.getString("prefix"));
                }
            }
        });
    }

    public void savePrefixes() {
        Prefix.getPrefixes().forEach(Prefix::savePrefixMainThread);
    }
}
