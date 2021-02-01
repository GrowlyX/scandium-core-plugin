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
                new Prefix(document.getString("id"),document.getString("name"), document.getString("displayName"), document.getString("prefix"));
            }
        });
    }

    public void savePrefixes() {
        Prefix.getPrefixes().forEach(Prefix::savePrefixMainThread);
    }
}
