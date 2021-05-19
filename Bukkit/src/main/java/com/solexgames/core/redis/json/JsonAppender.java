package com.solexgames.core.redis.json;

import com.solexgames.core.CorePlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GrowlyX
 * @since 3/1/2021
 * <p>
 * Creates a JSON chain of all the available parameters
 */

@Getter
public class JsonAppender {

    private final Map<String, String> parameters = new HashMap<>();

    private final String packet;

    public JsonAppender(String packet) {
        this.packet = packet;
    }

    public JsonAppender(Enum<?> aEnum) {
        this.packet = aEnum.name();
    }

    public JsonAppender put(String key, Object value) {
        this.parameters.put(key, value == null ? null : value.toString());
        return this;
    }

    public JsonAppender put(String key, String value) {
        this.parameters.put(key, value);
        return this;
    }

    public JsonAppender put(String key, Boolean value) {
        this.parameters.put(key, value.toString());
        return this;
    }

    public JsonAppender put(String key, Integer value) {
        this.parameters.put(key, value.toString());
        return this;
    }

    public JsonAppender put(String key, Character value) {
        this.parameters.put(key, value.toString());
        return this;
    }

    public JsonAppender put(String key, Long value) {
        this.parameters.put(key, value.toString());
        return this;
    }

    public JsonAppender put(String key, Float value) {
        this.parameters.put(key, value.toString());
        return this;
    }

    public JsonAppender put(String key, Double value) {
        this.parameters.put(key, value.toString());
        return this;
    }

    public String getParam(String key) {
        if (this.parameters.containsKey(key)) {
            return this.parameters.get(key);
        }

        return null;
    }

    public String getAsJson() {
        return CorePlugin.GSON.toJson(this);
    }
}
