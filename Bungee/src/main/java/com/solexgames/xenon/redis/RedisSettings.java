package com.solexgames.xenon.redis;

import lombok.Data;

/**
 * @author GrowlyX
 * @since 3/1/2021
 * <p>
 * Holds jedis settings data.
 */

@Data
public class RedisSettings {

    private final String hostAddress;
    private final int port;
    private final boolean auth;
    private final String password;

    /**
     * Constructor to initialize jedis values for {@link RedisManager}
     *
     * @param hostAddress Redis host address/ip address
     * @param port Redis host port
     * @param auth Redis authentication enabled/disabled
     * @param password Redis authentication password
     */
    public RedisSettings(String hostAddress, int port, boolean auth, String password) {
        this.hostAddress = hostAddress;
        this.port = port;
        this.auth = auth;
        this.password = password;
    }
}
