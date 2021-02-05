package vip.potclub.core.protocol.extend;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.protocol.TabInterceptor;

public class ProtocolTabInterceptor extends TabInterceptor {

    private PacketListener listener;

    public ProtocolTabInterceptor(CorePlugin plugin) {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                listener = new PacketAdapter(plugin, ConnectionSide.SERVER_SIDE, Packets.Server.TAB_COMPLETE) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        event.setCancelled(isCompletionCancelled(
                                event.getPlayer(),
                                event.getPacket().getStrings().read(0)
                        ));
                    }
                });
    }

    @Override
    public void close() {
        if (listener != null) {
            ProtocolLibrary.getProtocolManager().removePacketListener(listener);
            listener = null;
        }
    }
}
