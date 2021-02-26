package com.solexgames.core.manager;

import com.mongodb.Block;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.warps.Warp;
import com.solexgames.core.util.LocationUtil;
import org.bson.Document;

public class WarpManager {

    public WarpManager() {
        this.loadAllWarps();
        CorePlugin.getInstance().getLogger().info("[Warps] Loaded all warps.");
    }

    public void loadAllWarps() {
        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getWarpCollection().find().forEach((Block<? super Document>) warpDocument -> {
            if (Warp.getByName(warpDocument.getString("name")) == null) {
                new Warp(warpDocument.getString("name"), LocationUtil.getLocationFromString(warpDocument.getString("location")).orElse(null), warpDocument.getString("_id"));
            }
        }));
    }

    public void saveWarps() {
        Warp.getWarps().forEach(Warp::saveMainThread);
    }
}
