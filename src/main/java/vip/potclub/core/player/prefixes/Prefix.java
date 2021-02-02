package vip.potclub.core.player.prefixes;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.ranks.Rank;
import vip.potclub.core.util.SaltUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Prefix {

    @Getter
    private static List<Prefix> prefixes = new ArrayList<>();

    private String id;
    private String name;
    private String displayName;
    private String prefix;

    public Prefix(String id, String name, String displayName, String prefix) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.prefix = prefix;

        prefixes.add(this);
    }

    public Prefix(String name, String prefix) {
        this.id = SaltUtil.getRandomSaltedString();
        this.name = name;
        this.displayName = name;
        this.prefix = prefix;

        prefixes.add(this);
    }

    public void savePrefix() {
        Document document = new Document("_id", this.id);

        document.put("name", this.name);
        document.put("displayName", this.displayName);
        document.put("prefix", this.prefix);

        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPrefixCollection().replaceOne(Filters.eq("_id", this.id), document, new ReplaceOptions().upsert(true)));
    }

    public void savePrefixMainThread() {
        Document document = new Document("_id", this.id);

        document.put("name", this.name);
        document.put("displayName", this.displayName);
        document.put("prefix", this.prefix);

        CorePlugin.getInstance().getCoreDatabase().getPrefixCollection().replaceOne(Filters.eq("_id", this.id), document, new ReplaceOptions().upsert(true));
    }

    public String toJson() {
        return CorePlugin.GSON.toJson(this);
    }

    public static Prefix getByName(String name) {
        return prefixes.stream().filter(prefix -> prefix.getName().equals(name)).findFirst().orElse(null);
    }

    public static Prefix getById(String id) {
        return prefixes.stream().filter(prefix -> prefix.getId().equals(id)).findFirst().orElse(null);
    }
}
