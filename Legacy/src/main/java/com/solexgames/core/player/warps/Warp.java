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
import java.util.concurrent.CompletableFuture;

/**
 * @author GrowlyX
 * @since March 2021
 */

@Getter
@Setter
public class Warp {

    @Getter
    private static List<Warp> warps = new ArrayList<>();

    private String id;
    private String name;
    private String server;
    private Location location;

    public Warp(String name, Location location, String id, String server) {
        this.name = name;
        this.location = location;
        this.id = id;
        this.server = server;

        Warp.getWarps().add(this);
    }

    public Warp(String name, Location location, String server) {
        this.name = name;
        this.location = location;
        this.id = SaltUtil.getRandomSaltedString();
        this.server = server;

        Warp.getWarps().add(this);
    }

    public void saveWarp() {
        Document document = new Document("_id", this.id);

        document.put("server", this.server);
        document.put("name", this.name);
        document.put("location", LocationUtil.getStringFromLocation(this.location).orElse(null));

        CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getWarpCollection().replaceOne(Filters.eq("_id", this.id), document, new ReplaceOptions().upsert(true)));
    }

    public void saveMainThread() {
        Document document = new Document("_id", this.id);

        document.put("server", this.server);
        document.put("name", this.name);
        document.put("location", LocationUtil.getStringFromLocation(this.location).orElse(null));

        CorePlugin.getInstance().getCoreDatabase().getWarpCollection().replaceOne(Filters.eq("_id", this.id), document, new ReplaceOptions().upsert(true));
    }

    public static Warp getById(String id) {
        return Warp.getWarps().stream()
                .filter(warp -> warp.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static Warp getByName(String name) {
        return Warp.getWarps().stream()
                .filter(warp -> warp.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
