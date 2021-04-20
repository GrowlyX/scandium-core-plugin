package com.solexgames.core.hooks.protocol.extend;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.hooks.protocol.AbstractPacketHandler;
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
                    } catch (Exception ignored) { }
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
            public void onPacketSending(PacketEvent e){
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
    private boolean sendDemoScreen(Player player) {
        final PacketContainer packet = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);

        packet.getIntegers().write(0, 5);
        packet.getFloat().write(0, (float) 0);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            return true;
        } catch (InvocationTargetException ignored) {
            return false;
        }
    }
}
