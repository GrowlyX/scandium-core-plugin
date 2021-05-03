package com.solexgames.core.hooks.nms;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.command.BukkitCommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public interface INMS {

    void removeExecute(Player player);
    void addExecute(Player player);

    void updateTablist();
    void setupTablist(Player player);

    void updatePlayer(Player player);
    void updateCache(Player player);

    default void swapCommandMap() {
        try {
            final Field commandMapField = CorePlugin.getInstance().getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            final Object oldCommandMap = commandMapField.get(CorePlugin.getInstance().getServer());
            final BukkitCommandMap newCommandMap = new BukkitCommandMap(CorePlugin.getInstance().getServer());

            final Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);

            final Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(knownCommandsField, knownCommandsField.getModifiers() & -17);

            knownCommandsField.set(newCommandMap, knownCommandsField.get(oldCommandMap));
            commandMapField.set(CorePlugin.getInstance().getServer(), newCommandMap);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
