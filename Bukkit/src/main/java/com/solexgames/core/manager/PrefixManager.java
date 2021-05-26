package com.solexgames.core.manager;

import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.prefixes.Prefix;
import org.bson.Document;

import java.util.concurrent.CompletableFuture;

public class PrefixManager {

    public PrefixManager() {
        if (CorePlugin.getInstance().getConfig().getBoolean("chat-tags.add-default")) {
            this.createDefaultPrefixes();
        }

        this.loadPrefixes();
    }

    private void createDefaultPrefixes() {
        CompletableFuture.runAsync(() -> {
            if (CorePlugin.getInstance().getCoreDatabase().getPrefixCollection().find(Filters.eq("name", "Verified")).first() == null) {
                new Prefix("Verified", "&2✔");
            }
            if (CorePlugin.getInstance().getCoreDatabase().getPrefixCollection().find(Filters.eq("name", "Liked")).first() == null) {
                new Prefix("Liked", "&b✔");
            }
        });
    }

    public void loadPrefixes() {
        CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPrefixCollection().find().forEach((Block<? super Document>) document -> {
            if (Prefix.getByName(document.getString("name")) == null) {
                final Prefix prefix = new Prefix(document.getString("_id"), document.getString("name"), document.getString("displayName"), document.getString("prefix"));

                if (document.getBoolean("purchasable") != null) {
                    prefix.setPurchasable(document.getBoolean("purchasable"));
                }
            }
        }));
    }

    public void savePrefixes() {
        Prefix.getPrefixes().forEach(Prefix::savePrefix);
    }
}
