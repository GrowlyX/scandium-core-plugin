package vip.potclub.core.manager;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.prefixes.Prefix;

@Getter
@Setter
public class PrefixManager {

    public PrefixManager() {
        this.loadPrefixes();
        CorePlugin.getInstance().getLogger().info("[Prefixes] Loaded all prefixes.");
    }

    public void loadPrefixes() {
        CorePlugin.getInstance().getMongoThread().execute(() -> {
            for (Document document : CorePlugin.getInstance().getCoreDatabase().getPrefixCollection().find()) {
                new Prefix(document.getString("_id"),document.getString("name"), document.getString("displayName"), document.getString("prefix"));
            }
        });

        /*
        if (Prefix.getPrefixes().isEmpty()) {
            CorePlugin.getInstance().getMongoThread().execute(() -> {
                new Prefix("Verified", "&2✔");
                new Prefix("Liked", "&b✔");
                new Prefix("Love", "&c❤");
                new Prefix("Star", "&6✫");
            });
            CorePlugin.getInstance().getLogger().info("[Prefixes] Created four new prefixes. (Verified, Liked, Love, Star)");
        }
        */
    }

    public void savePrefixes() {
        Prefix.getPrefixes().forEach(Prefix::savePrefixMainThread);
    }
}
