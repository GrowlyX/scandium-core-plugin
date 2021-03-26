package com.solexgames.core.player.callback;

import org.bson.Document;

public interface FetchCallback {

    void onCompletion(Document result);

}
