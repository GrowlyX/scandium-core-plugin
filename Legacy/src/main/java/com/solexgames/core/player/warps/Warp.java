package com.solexgames.core.player.warps;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.LocationUtil;
import com.solexgames.core.util.SaltUtil;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Warp {

    @Getter
    private static List<Warp> warps = new ArrayList<>();

    private String id;
    private String name;
    private Location location;

    public Warp(String name, Location location, String id) {
        this.name = name;
        this.location = location;
        this.id = id;

        Warp.getWarps().add(this);
    }

    public Warp(String name, Location location) {
        this.name = name;
        this.location = location;
        this.id = SaltUtil.getRandomSaltedString();

        Warp.getWarps().add(this);
    }

    public void saveWarp() {
        Document document = new Document("_id", this.id);

        document.put("name", this.name);
        document.put("location", LocationUtil.getStringFromLocation(this.location));

        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getWarpCollection().replaceOne(Filters.eq("_id", this.id), document, new ReplaceOptions().upsert(true)));
    }

    public void saveMainThread() {
        Document document = new Document("_id", this.id);

        document.put("name", this.name);
        document.put("location", LocationUtil.getStringFromLocation(this.location));

        CorePlugin.getInstance().getCoreDatabase().getWarpCollection().replaceOne(Filters.eq("_id", this.id), document, new ReplaceOptions().upsert(true));
    }

    public static Warp getById(String id) {
        return getWarps().stream().filter(warp -> warp.getId().equals(id)).findFirst().orElse(null);
    }

    public static Warp getByName(String name) {
        return getWarps().stream().filter(warp -> warp.getName().equals(name)).findFirst().orElse(null);
    }
}
