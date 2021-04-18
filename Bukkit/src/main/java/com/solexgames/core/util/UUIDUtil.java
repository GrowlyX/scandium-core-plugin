package com.solexgames.core.util;

import com.solexgames.core.CorePlugin;
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
import java.util.stream.Collectors;

/**
 * @author GrowlyX & Some github person
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
            HttpGet request = new HttpGet("https://sessionserver.mojang.com/session/minecraft/profile/" + formattedUUID);
            HttpResponse response = CorePlugin.getInstance().getHttpClient().execute(request);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String textResponse = bufferedReader.lines().collect(Collectors.joining(" "));

            Map map = CorePlugin.GSON.fromJson(textResponse, Map.class);
            return map.get("name").toString();

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
        final UUID uuid = CorePlugin.getInstance().getUuidCache().get(name);

        if (uuid != null) {
            return uuid;
        }

        try {
            HttpPost request = new HttpPost("https://api.mojang.com/profiles/minecraft");

            // create the entity
            StringEntity entity = new StringEntity("[\"" + name + "\"]");
            request.setEntity(entity);
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            // send the request
            HttpResponse response = CorePlugin.getInstance().getHttpClient().execute(request);

            // read the response
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String textResponse = bufferedReader.lines().collect(Collectors.joining(" "));

            // parse the response
            List list = CorePlugin.GSON.fromJson(textResponse, List.class);
            Map profile = (Map) list.get(0);
            String stringUUID = (String) profile.get("id");

            return UUID.fromString(formatUUID(stringUUID));

        } catch (Exception exception) {
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
    public static String formatUUID(String id) {
        return id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32);
    }
}
