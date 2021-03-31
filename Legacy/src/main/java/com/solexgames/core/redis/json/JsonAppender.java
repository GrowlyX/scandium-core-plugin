package com.solexgames.core.redis.json;

import com.google.gson.Gson;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.redis.action.RedisAction;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GrowlyX
 * @since 3/1/2021
 * <p>
 * Holds instances to anything redis related.
 */

@Getter
public class JsonAppender {

    private final Map<String, String> parameters;
    private final RedisAction packet;

    /**
     * Creates a new instance of {@link JsonAppender} with a packet type {@link RedisAction}
     *
     * @param packet Redis packet type for this instance.
     */
    public JsonAppender(RedisAction packet) {
        this.packet = packet;
        this.parameters = new HashMap<>();
    }

    public JsonAppender put(String key, String value) {
        this.parameters.put(key, value);
        return this;
    }

    public String getParam(String key) {
        if (containsParam(key)) {
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
