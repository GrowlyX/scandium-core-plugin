package vip.potclub.core.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;

public final class UUIDUtil {

    public static Map.Entry<UUID, String> getUUID(String name) throws IOException, ParseException {
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
        URLConnection conn = url.openConnection();

        conn.setDoOutput(true);

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(reader.readLine());

        UUID uuid = UUID.fromString(String.valueOf(obj.get("id")).replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        name = String.valueOf(obj.get("name"));
        reader.close();

        return new AbstractMap.SimpleEntry<>(uuid, name);
    }
}
