package com.solexgames.core.protocol.extend;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.protocol.AbstractChatInterceptor;
import com.solexgames.core.util.Color;

public class ProtocolChatInterceptor extends AbstractChatInterceptor {

    protected PacketAdapter adapter;

    @Override
    public void initializePacketInterceptor() {
        this.adapter = new PacketAdapter(CorePlugin.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Client.TAB_COMPLETE) {
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType().equals(PacketType.Play.Client.TAB_COMPLETE)) {
                    try {
                        if (event.getPlayer().hasPermission("scandium.tabcomplete.bypass")) return;

                        PacketContainer packet = event.getPacket();
                        String message = packet.getSpecificModifier(String.class).read(0).toLowerCase();

                        if (((message.startsWith("/")) && (!message.contains(" "))) || ((message.startsWith("/ver")) && (!message.contains("  "))) || ((message.startsWith("/version")) && (!message.contains("  "))) || ((message.startsWith("/?")) && (!message.contains("  "))) || ((message.startsWith("/about")) && (!message.contains("  "))) || ((message.startsWith("/help")) && (!message.contains("  ")))) {
                            event.setCancelled(true);
                        }
                    } catch (Exception ignored) { }
                }
            }
        };

        if (this.getConfig().getBoolean("tab-block.enabled")) {
            ProtocolLibrary.getProtocolManager().addPacketListener(adapter);
        }
    }
}
