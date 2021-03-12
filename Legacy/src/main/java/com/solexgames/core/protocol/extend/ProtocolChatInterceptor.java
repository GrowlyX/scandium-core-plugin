package com.solexgames.core.protocol.extend;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.protocol.AbstractChatInterceptor;

public class ProtocolChatInterceptor extends AbstractChatInterceptor {

    protected PacketAdapter adapter;
    protected PacketAdapter sendAdapter;
    protected String[] returnString;

    @Override
    public void initializePacketInterceptor() {
        this.returnString = CorePlugin.getInstance().getConfig().getStringList("tab-block.return").toArray(new String[0]);
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
            ProtocolLibrary.getProtocolManager().addPacketListener(adapter);
        }

        ProtocolLibrary.getProtocolManager().addPacketListener(sendAdapter);
    }
}
