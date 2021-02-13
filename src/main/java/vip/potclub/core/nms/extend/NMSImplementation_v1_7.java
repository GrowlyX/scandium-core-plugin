package vip.potclub.core.nms.extend;

import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import vip.potclub.core.nms.AbstractNMSImplementation;

public class NMSImplementation_v1_7 extends AbstractNMSImplementation {

    @Override
    public void removeExecute(Player player) {
        MinecraftServer.getServer().getPlayerList().sendAll(PacketPlayOutPlayerInfo.removePlayer((((CraftPlayer) player).getHandle())));
    }

    @Override
    public void addExecute(Player player) {
        MinecraftServer.getServer().getPlayerList().sendAll(PacketPlayOutPlayerInfo.addPlayer((((CraftPlayer) player).getHandle())));
    }
}
