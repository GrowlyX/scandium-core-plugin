package vip.potclub.core.protocol.extend;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.Multimap;
import vip.potclub.core.protocol.TabInterceptor;

public class PacketTabInterceptor extends TabInterceptor implements Listener {

    private class ProxyList extends ForwardingList<Object> {
        private Player player;
        private List<Object> original;

        public ProxyList(Player player, List<Object> original) {
            this.player = player;
            this.original = original;
        }

        @Override
        protected List<Object> delegate() {
            return original;
        }

        @Override
        public boolean add(Object element) {
            add(size(), element);
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Object> collection) {
            return super.addAll(process(collection));
        }

        @Override
        public void add(int index, Object element) {
            final Object packet = interceptPacket(player, element);

            if (packet != null) {
                super.add(index, packet);
            }
        }

        @Override
        public Object set(int index, Object element) {
            final Object packet = interceptPacket(player, element);

            if (packet != null) {
                return super.set(index, packet);
            }
            return get(index);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Object> elements) {
            return super.addAll(index, process(elements));
        }

        private Collection<Object> process(Collection<? extends Object> iterable) {
            // Transform -> Apply our intercept function to each element in the list
            // Filter -> Remove all null packets
            return Collections2.filter(Collections2.transform(iterable, new Function<Object, Object>() {
                @Override
                public Object apply(@Nullable Object packet) {
                    return interceptPacket(player, packet);
                }
            }), Predicates.notNull());
        }
    }

    /**
     * Represents a field set operation.
     * @author Kristian
     */
    private static class FieldSetter {
        private final Field field;
        private final Object target;
        private final List<Object> applyValue;

        // The old value
        private final WeakReference<List<Object>> creationValue;

        public static FieldSetter from(Field field, Object target, List<Object> value) throws IllegalAccessException {
            return new FieldSetter(field, target, value);
        }

        private FieldSetter(Field field, Object target, List<Object> value) throws IllegalAccessException {
            this.field = field;
            this.target = target;
            this.applyValue = value;
            this.creationValue = new WeakReference<List<Object>>(getCurrentValue());
        }

        /**
         * Retrieve the current value of the field.
         * @return The current value.
         */
        @SuppressWarnings("unchecked")
        public List<Object> getCurrentValue() throws IllegalAccessException {
            return (List<Object>) field.get(target);
        }

        /**
         * Retrieve the value of the field as it appeared when the setter was created.
         * @return The creation value.
         */
        public List<Object> getCreationValue() {
            return creationValue.get();
        }

        /**
         * Apply the field operation.
         * @return A field setter that reverts this operation.
         * @throws IllegalArgumentException Cannot assign a value of this type.
         */
        public FieldSetter apply() throws IllegalArgumentException {
            try {
                field.set(target, applyValue);
                return new FieldSetter(field, target, getCreationValue());
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to access field.", e);
            }
        }
    }

    // Method for injecting players
    private Method getHandleMethod;
    private Field connectionField;
    private Field networkField;
    private Field highPriorityQueueField;
    private Field lowPriorityQueueField;

    private Field completedCommandField;

    // The packet class
    private Class<?> tabCompletePacket;
    private Multimap<Player, FieldSetter> revertOperations = ArrayListMultimap.create();

    // The parent plugin
    private Plugin plugin;

    // Whether or not we have detected interfering plugins
    private boolean detectedInterference;

    public PacketTabInterceptor(Plugin plugin) {
        // Register this as a listener
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Invoked when we have intercepted a packet.
     * @param packet - the packet to intercept, or NULL to skip.
     */
    private Object interceptPacket(Player player, Object packet) {
        Class<?> clazz = packet.getClass();

        if (tabCompletePacket == null && clazz.getSimpleName().equals("Packet203TabComplete")) {
            tabCompletePacket = clazz;
        }
        if (tabCompletePacket != null && tabCompletePacket.isAssignableFrom(clazz)) {
            // Find the first string field in the packet
            if (completedCommandField == null) {
                for (Field field : tabCompletePacket.getDeclaredFields()) {
                    if (field.getType().equals(String.class)) {
                        completedCommandField = field;
                        completedCommandField.setAccessible(true);
                    }
                }
            }

            try {
                // Filter the packet if the completion is not allowed
                if (isCompletionCancelled(player, (String) completedCommandField.get(packet))) {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return packet;
    }

    @SuppressWarnings("unchecked")
    private void injectPlayer(Player player) throws Exception {
        // Cannot inject twice
        if (revertOperations.containsKey(player))
            throw new IllegalArgumentException("Cannot inject "+ player + "twice");

        Object nmsPlayer = getNmsPlayer(player);

        if (connectionField == null)
            connectionField = getField(nmsPlayer, nmsPlayer.getClass(), "playerConnection");
        Object connection = connectionField.get(nmsPlayer);

        if (networkField == null)
            networkField = getField(connection, connection.getClass(), "networkManager");
        Object networkManager = networkField.get(connection);

        if (highPriorityQueueField == null)
            highPriorityQueueField = getField(networkManager, networkManager.getClass(), "highPriorityQueue");
        if (lowPriorityQueueField == null)
            lowPriorityQueueField = getField(networkManager, networkManager.getClass(), "lowPriorityQueue");
        List<Object> highPriorityQueue = (List<Object>) highPriorityQueueField.get(networkManager);
        List<Object> lowPriorityQueue = (List<Object>) lowPriorityQueueField.get(networkManager);

        // Proxy the lists
        revertOperations.put(player,
                FieldSetter.from(highPriorityQueueField, networkManager, new ProxyList(player, highPriorityQueue)).apply()
        );
        revertOperations.put(player,
                FieldSetter.from(lowPriorityQueueField, networkManager, new ProxyList(player, lowPriorityQueue)).apply()
        );
    }

    private void uninjectPlayer(Player player) {
        for (FieldSetter setter : revertOperations.removeAll(player)) {
            setter.apply();
        }
    }

    private Object getNmsPlayer(Player player) throws Exception {
        if (getHandleMethod == null) {
            getHandleMethod = getMethod(0, Modifier.STATIC, player.getClass(), "getHandle");
        }
        return getHandleMethod.invoke(player);
    }

    @Override
    public void close() {
        // Clear as a listener
        HandlerList.unregisterAll(this);

        // Revert all proxy lists
        for (FieldSetter setter : revertOperations.values()) {
            setter.apply();
        }
        revertOperations.clear();
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        final Player player = e.getPlayer();

        // Wait until the playerConnection has been assigned
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    injectPlayer(player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1L);

        // Wait a second and see if the lists remain the same
        if (!detectedInterference) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    // Check every field
                    for (FieldSetter setter : revertOperations.get(player)) {
                        try {
                            if (setter.getCreationValue() != setter.getCurrentValue()) {
                                detectInterference(setter);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, 20L);
        }
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent e) {
        uninjectPlayer(e.getPlayer());
    }

    /**
     * Invoked when we have detected interference.
     */
    protected void detectInterference(FieldSetter setter) throws IllegalAccessException {
        plugin.getLogger().warning("Detected interfering plugin(s). Field value: " + setter.getCurrentValue());
        plugin.getLogger().warning("Please install ProtocolLib.");
        detectedInterference = true;
    }

    /**
     * Search for the first publically and privately defined method of the given name and parameter count.
     * @param requireMod - modifiers that are required.
     * @param bannedMod - modifiers that are banned.
     * @param clazz - a class to start with.
     * @param methodName - the method name, or NULL to skip.
     * @return The first method by this name.
     * @throws IllegalStateException If we cannot find this method.
     */
    private static Method getMethod(int requireMod, int bannedMod, Class<?> clazz, String methodName, Class<?>... params) {
        for (Method method : clazz.getDeclaredMethods()) {
            // Limitation: Doesn't handle overloads
            if ((method.getModifiers() & requireMod) == requireMod &&
                    (method.getModifiers() & bannedMod) == 0 &&
                    (methodName == null || method.getName().equals(methodName)) &&
                    Arrays.equals(method.getParameterTypes(), params)) {

                method.setAccessible(true);
                return method;
            }
        }
        // Search in every superclass
        if (clazz.getSuperclass() != null)
            return getMethod(requireMod, bannedMod, clazz.getSuperclass(), methodName, params);
        throw new IllegalStateException(String.format(
                "Unable to find method %s (%s).", methodName, Arrays.asList(params)));
    }

    /**
     * Search for the first publically and privately defined field of the given name.
     * @param instance - an instance of the class with the field.
     * @param clazz - an optional class to start with, or NULL to deduce it from instance.
     * @param fieldName - the field name.
     * @return The first field by this name.
     * @throws IllegalStateException If we cannot find this field.
     */
    private static Field getField(Object instance, Class<?> clazz, String fieldName) {
        if (clazz == null)
            clazz = instance.getClass();
        // Ignore access rules
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                field.setAccessible(true);
                return field;
            }
        }
        // Recursively find the correct field
        if (clazz.getSuperclass() != null)
            return getField(instance, clazz.getSuperclass(), fieldName);
        throw new IllegalStateException("Unable to find field " + fieldName + " in " + instance);
    }
}
