package com.solexgames.core.redis;

import com.solexgames.core.redis.handler.JedisHandler;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * @author GrowlyX
 * @since 5/18/2021
 * <p>
 * Builds a new instance of {@link JedisManager}
 */

@Getter
@NoArgsConstructor
public class JedisBuilder {

    private JedisSettings settings;
    private JedisHandler handler;

    private String channel;

    public JedisBuilder withSettings(JedisSettings settings) {
        this.settings = settings;
        return this;
    }

    public JedisBuilder withHandler(JedisHandler handler) {
        this.handler = handler;
        return this;
    }

    public JedisBuilder withChannel(String channel) {
        this.channel = channel;
        return this;
    }

    @SneakyThrows
    public JedisManager build() {
        if (this.settings == null) {
            throw new RuntimeException("Jedis settings is null");
        }

        return new JedisManager(this.channel == null ? "jedis" : this.channel, this.settings, this.handler == null ? null : this.handler);
    }
}
