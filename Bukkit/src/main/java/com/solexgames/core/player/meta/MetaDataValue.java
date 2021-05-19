package com.solexgames.core.player.meta;

import lombok.Data;

/**
 * @author GrowlyX
 * @since 5/19/2021
 */

@Data
public class MetaDataValue {

    private final Object object;

    public String getAsString() {
        return (String) this.object;
    }

    public Boolean getAsBoolean() {
        return (Boolean) this.object;
    }

    public Long getAsLong() {
        return (Long) this.object;
    }
}
