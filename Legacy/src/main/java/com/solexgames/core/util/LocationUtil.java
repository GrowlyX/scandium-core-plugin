package com.solexgames.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Optional;

public final class LocationUtil {

    private static final String SEPARATOR = "_S_";

    public static Optional<String> getStringFromLocation(Location location) {
        if (location != null) {
            return Optional.of(location.getWorld().getName() + SEPARATOR + location.getX() + SEPARATOR + location.getY() + SEPARATOR + location.getZ() + SEPARATOR + location.getYaw() + SEPARATOR + location.getPitch());
        }
        return Optional.empty();
    }

    public static Optional<Location> getLocationFromString(String string) {
        if (string != null && !string.trim().equals("")) {
            String[] args = string.split(SEPARATOR);

            if (args.length == 6) {
                World world = Bukkit.getServer().getWorld(args[0]);

                double xCoord = Double.parseDouble(args[1]);
                double yCoord = Double.parseDouble(args[2]);
                double zCoord = Double.parseDouble(args[3]);
                float yawFloat = Float.parseFloat(args[4]);
                float pitchFloat = Float.parseFloat(args[5]);

                return Optional.of(new Location(world, xCoord, yCoord, zCoord, yawFloat, pitchFloat));
            }
        }
        return Optional.empty();
    }
}
