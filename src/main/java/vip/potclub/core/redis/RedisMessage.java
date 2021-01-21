package vip.potclub.core.redis;

import com.google.gson.Gson;
import lombok.Getter;
import vip.potclub.core.enums.RedisPacketType;

import java.util.HashMap;
import java.util.Map;

@Getter
public class RedisMessage {

    private final RedisPacketType packet;
    private final Map<String, String> params;

    public RedisMessage(RedisPacketType packet) {
        this.packet = packet;
        this.params = new HashMap<>();
    }

    public RedisMessage setParam(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    public String getPacketString() {
        return this.packet.packetDataName;
    }

    public String getParam(String key) {
        if (containsParam(key)) {
            return this.params.get(key);
        }
        return null;
    }

    public boolean containsParam(String key) {
        return this.params.containsKey(key);
    }

    public void removeParam(String key) {
        if (containsParam(key)) {
            this.params.remove(key);
        }
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
