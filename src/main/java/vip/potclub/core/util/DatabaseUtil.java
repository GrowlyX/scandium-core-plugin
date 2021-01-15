package vip.potclub.core.util;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import vip.potclub.core.CorePlugin;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class DatabaseUtil {

    public static void saveDocument(Document document, UUID uuid) {
        Bson filter = Filters.eq("_id", uuid);
        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getDatabase().getPlayerCollection().replaceOne(filter, document, new ReplaceOptions().upsert(true)));
    }
}
