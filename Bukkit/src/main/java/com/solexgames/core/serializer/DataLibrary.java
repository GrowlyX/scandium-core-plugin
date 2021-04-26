package com.solexgames.core.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.manager.DataManager;
import lombok.Getter;

/**
 * @author GrowlyX
 * @since 4/20/2021
 */

@Getter
public final class DataLibrary {

    @Getter
    private static DataLibrary instance;

    private final Gson gson;

    private final DataManager dataManager = new DataManager();

    public DataLibrary() {
        final long milli = System.currentTimeMillis();

        this.gson = CorePlugin.GSON;

        instance = this;

        final long finalMilli = System.currentTimeMillis() - milli;

        System.out.println("[DataLibrary] Initialized DataLibrary in " + finalMilli + "ms!");
    }
}
