package com.solexgames.core.disguise;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.solexgames.core.CorePlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
@RequiredArgsConstructor
public class DisguiseData {

    private final UUID uuid;
    private final String name;

    private final String skin;
    private final String signature;

    public Document getDocument() {
        final Document document = new Document("_id", this.uuid);

        document.put("uuid", this.uuid.toString());
        document.put("name", this.name);
        document.put("skin", this.skin);
        document.put("signature", this.signature);

        return document;
    }

    public void saveData() {
        CompletableFuture.runAsync(() ->
                CorePlugin.getInstance().getCoreDatabase().getDisguiseCollection().replaceOne(
                        Filters.eq("_id", this.uuid),
                        this.getDocument(),
                        new ReplaceOptions().upsert(true)
                )
        );
    }
}
