package com.solexgames.core.manager;

import com.mongodb.Block;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.warps.Warp;
import com.solexgames.core.util.LocationUtil;
import org.bson.Document;

import java.util.concurrent.CompletableFuture;

public class WarpManager {

    public WarpManager() {
        this.loadAllWarps();
    }

    public void loadAllWarps() {
        CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getWarpCollection().find().forEach((Block<? super Document>) warpDocument -> {
            if (warpDocument.getString("server") != null) {
                if (Warp.getByName(warpDocument.getString("name")) == null) {
                    new Warp(warpDocument.getString("name"), LocationUtil.getLocationFromString(warpDocument.getString("location")).orElse(null), warpDocument.getString("_id"), warpDocument.getString("server"));
                }
            }
        }));
    }

    public void saveWarps() {
        Warp.getWarps().forEach(Warp::saveMainThread);
    }
}
