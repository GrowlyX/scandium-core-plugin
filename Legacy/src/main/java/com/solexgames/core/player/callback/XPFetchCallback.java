package com.solexgames.core.player.callback;

import com.mongodb.client.MongoCursor;
import org.bson.Document;

public interface XPFetchCallback {

    void onCompletion(MongoCursor<Document> result);

}
