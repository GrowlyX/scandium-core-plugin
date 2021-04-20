package com.solexgames.core.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.atomic.AtomicUUID;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author GrowlyX
 * @since 3/4/2021
 */

@UtilityClass
public final class UUIDUtil {

    /**
     * Fetches the name of a player from mojang's api
     *
     * @param uuid the UUID of the player
     * @return the name of the player
     */
    public static String fetchName(UUID uuid) {
        String formattedUUID = uuid.toString().replace("-", "");

        try {
            final URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + formattedUUID);
            final JsonObject json = new JsonParser().parse(new InputStreamReader(url.openStream())).getAsJsonObject();

            return json.get("name").toString().replace("\"", "");
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * Fetches the UUID of a player from mojang's api
     *
     * @param name the name of the player
     */
    public static UUID fetchUUID(String name) {
        try {
            final URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            final JsonObject json = new JsonParser().parse(new InputStreamReader(url.openStream())).getAsJsonObject();
            final String uuid = json.get("id").toString().replace("\"", "");

            return UUIDUtil.formatUUID(uuid);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * @param id UUID non-formatted
     * @return Formatted
     * @original https://github.com/desht/dhutils/blob/master/Lib/src/main/java/me/desht/dhutils/UUIDFetcher.java
     * <p>
     * Formats a UUID given by mojang and adds hyphens so it can be read by UUID#fromString
     */
    public static UUID formatUUID(String id) {
        return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
    }
}
