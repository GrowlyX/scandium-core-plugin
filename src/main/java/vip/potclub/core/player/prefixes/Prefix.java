package vip.potclub.core.player.prefixes;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.ranks.Rank;

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
    private String color;

    public Prefix(String id, String name, String displayName, String prefix, String color) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.prefix = prefix;
        this.color = color;

        prefixes.add(this);
    }

    public void savePrefix() {
        Document document = new Document("_id", this.id);

        document.put("id", this.id);
        document.put("name", this.name);
        document.put("displayName", this.displayName);
        document.put("prefix", this.prefix);
        document.put("color", this.color);

        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPrefixCollection().replaceOne(Filters.eq("_id", this.id), document, new ReplaceOptions().upsert(true)));
    }

    public void savePrefixMainThread() {
        Document document = new Document("_id", this.id);

        document.put("id", this.id);
        document.put("name", this.name);
        document.put("displayName", this.displayName);
        document.put("prefix", this.prefix);
        document.put("color", this.color);

        CorePlugin.getInstance().getCoreDatabase().getPrefixCollection().replaceOne(Filters.eq("_id", this.id), document, new ReplaceOptions().upsert(true));
    }

    public String toJson() {
        return CorePlugin.GSON.toJson(this);
    }
}
