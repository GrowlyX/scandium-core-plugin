package vip.potclub.core.redis;

import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.listener.RedisListener;

@Getter
@Setter
public class RedisClient {

    private JedisPool jedisPool;
    private RedisListener redisListener;

    private final String redisAddress;
    private final String redisPassword;
    private final int redisPort;

    private boolean redisAuthentication;
    private boolean isClientActive;

    public RedisClient() {
        this.redisAddress = CorePlugin.getInstance().getDatabaseConfig().getString("redis.host");
        this.redisPassword = CorePlugin.getInstance().getDatabaseConfig().getString("redis.authentication.password");
        this.redisPort = CorePlugin.getInstance().getDatabaseConfig().getInt("redis.port");
        this.redisAuthentication = CorePlugin.getInstance().getDatabaseConfig().getBoolean("redis.authentication.enabled");

        try {
            this.jedisPool = new JedisPool(redisAddress, redisPort);
            Jedis jedis = this.jedisPool.getResource();

            if (redisAuthentication){
                jedis.auth(this.redisPassword);
            }

            this.redisListener = new RedisListener();
            (new Thread(() -> jedis.subscribe(this.redisListener, "SCANDIUM"))).start();
            jedis.connect();
            this.setClientActive(true);

            CorePlugin.getInstance().getLogger().info("[Redis] Connected to Redis backend.");
        } catch (Exception e) {
            CorePlugin.getInstance().getLogger().severe("[Redis] Could not connect to Redis backend.");

            this.setClientActive(false);
        }
    }

    public void destroyClient() {
        try {
            jedisPool.destroy();
            this.redisListener.unsubscribe();
        } catch (Exception e) {
            System.out.println("[Redis] Could not destroy Redis Pool.");
        }
    }

    public void write(String json){
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.auth(this.redisPassword);
            jedis.publish("SCANDIUM", json);
        }
    }
}
