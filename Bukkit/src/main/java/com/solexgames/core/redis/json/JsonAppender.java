package com.solexgames.core.redis.json;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.redis.packet.RedisAction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GrowlyX
 * @since 3/1/2021
 */

@Getter
@RequiredArgsConstructor
public class JsonAppender {

    private final Map<String, String> parameters = new HashMap<>();

    private final RedisAction packet;

    public JsonAppender put(String key, String value) {
        this.parameters.put(key, value);
        return this;
    }

    public String getParam(String key) {
        if (this.containsParam(key)) {
            return this.parameters.get(key);
        }

        return null;
    }

    public boolean containsParam(String key) {
        return this.parameters.containsKey(key);
    }

    /**
     * Get the Json Appended String from the JsonAppender instance
     *
     * @return The appended json string
     */
    public String getAppended() {
        return CorePlugin.GSON.toJson(this);
    }
}
