package com.solexgames.core.serializer.impl;

import com.solexgames.core.serializer.DataSerializer;
import org.bukkit.Location;

/**
 * @author GrowlyX
 * @since 4/20/2021
 */

public class LocationSerializer extends DataSerializer<Location> {

    @Override
    public Class<Location> getClazz() {
        return Location.class;
    }
}
