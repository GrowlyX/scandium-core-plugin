package vip.potclub.core.manager;

import com.mongodb.Block;
import org.bson.Document;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.warps.Warp;
import vip.potclub.core.util.LocationUtil;

public class WarpManager {

    public WarpManager() {
        this.loadAllWarps();
        CorePlugin.getInstance().getLogger().info("[Warps] Loaded all warps.");
    }

    public void loadAllWarps() {
        CorePlugin.getInstance().getMongoThread().execute(() -> {
            CorePlugin.getInstance().getCoreDatabase().getWarpCollection().find().forEach((Block<? super Document>) warpDocument -> {
                if (Warp.getByName(warpDocument.getString("name")) == null) {
                    new Warp(warpDocument.getString("name"), LocationUtil.getLocationFromString(warpDocument.getString("location")), warpDocument.getString("_id"));
                }
            });
        });
    }

    public void saveWarps() {
        Warp.getWarps().forEach(Warp::saveMainThread);
    }
}
