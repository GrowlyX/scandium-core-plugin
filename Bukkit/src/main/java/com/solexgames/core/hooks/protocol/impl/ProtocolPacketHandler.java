package com.solexgames.core.hooks.protocol.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.hooks.protocol.AbstractPacketHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class ProtocolPacketHandler extends AbstractPacketHandler {

    protected PacketAdapter adapter;
    protected PacketAdapter sendAdapter;
    protected PacketAdapter outPlay;

    protected String[] returnString;

    @Override
    public void initializePacketHandlers() {
        this.returnString = CorePlugin.getInstance().getConfig().getStringList("tab-block.return").toArray(new String[0]);
        this.adapter = new PacketAdapter(CorePlugin.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Client.TAB_COMPLETE) {
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType().equals(PacketType.Play.Client.TAB_COMPLETE)) {
                    try {
                        if (event.getPlayer().hasPermission("scandium.tabcomplete.bypass")) return;

                        final PacketContainer packet = event.getPacket();
                        final String message = packet.getSpecificModifier(String.class).read(0).toLowerCase();

                        if (((message.startsWith("/")) && (!message.contains(" "))) || ((message.startsWith("/ver")) && (!message.contains("  "))) || ((message.startsWith("/version")) && (!message.contains("  "))) || ((message.startsWith("/?")) && (!message.contains("  "))) || ((message.startsWith("/about")) && (!message.contains("  "))) || ((message.startsWith("/help")) && (!message.contains("  ")))) {
                            event.setCancelled(true);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        };

        this.outPlay = new PacketAdapter(CorePlugin.getInstance(), ListenerPriority.HIGHEST, PacketType.Status.Server.OUT_SERVER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                final WrappedServerPing serverPing = event.getPacket().getServerPings().read(0);

                serverPing.setVersionProtocol(-1332);
                serverPing.setVersionName("CheatBreaker");
            }
        };

        this.sendAdapter = new PacketAdapter(CorePlugin.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.TAB_COMPLETE) {
            public void onPacketSending(PacketEvent e) {
                if (e.getPacketType() == PacketType.Play.Server.TAB_COMPLETE) {
                    if (!e.getPlayer().hasPermission("scandium.tabcomplete.bypass")) {
                        e.getPacket().getStringArrays().write(0, returnString);
                    }
                }
            }
        };

        if (this.getConfig().getBoolean("tab-block.enabled")) {
            ProtocolLibrary.getProtocolManager().addPacketListener(this.adapter);
        }
        if (this.getConfig().getBoolean("tab-block.return-enabled")) {
            ProtocolLibrary.getProtocolManager().addPacketListener(this.sendAdapter);
        }
    }

    /**
     * From Bukkit Forums - https://bukkit.org/threads/how-to-open-demo-minecraft-gui.407815/
     */
    @Override
    public boolean sendDemoScreen(Player player) {
        final PacketContainer packet = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);

        packet.getIntegers().write(0, 5);
        packet.getFloat().write(0, (float) 0);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean turnPlayer(Player target) {
        String n;
        double x;
        double y;
        double z;

        n = target.getLocation().getWorld().getName();
        x = target.getLocation().getX();
        y = target.getLocation().getY();
        z = target.getLocation().getZ();

        double yaw = target.getLocation().getYaw();
        double pitch = target.getLocation().getPitch();

        World w = Bukkit.getWorld(n);

        double newYaw;
        Location newLocation;

        if (yaw > 0.0D) {
            newYaw = yaw - 180.0D;
            newLocation = new Location(w, x, y, z, (float) newYaw, (float) pitch);
            target.teleport(newLocation);
        }

        if (yaw < 0.0D) {
            newYaw = 180.0D + yaw;
            newLocation = new Location(w, x, y, z, (float) newYaw, (float) pitch);
            target.teleport(newLocation);
        }

        if (yaw == 0.0D || yaw == 0.0D) {
            newLocation = new Location(w, x, y, z, 180.0F, (float) pitch);
            target.teleport(newLocation);
        }

        if (yaw == 180.0D || yaw == -180.0D) {
            newLocation = new Location(w, x, y, z, 0.0F, (float) pitch);
            target.teleport(newLocation);
        }

        return false;
    }
}
