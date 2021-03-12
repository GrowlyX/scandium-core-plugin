package com.solexgames.core.abstraction.access.extend;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.abstraction.access.AbstractNMSAccess;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

public class NMSAccess_v1_16 extends AbstractNMSAccess {

    @Override
    public void removeExecute(Player player) {
        PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, (((CraftPlayer) player).getHandle()));
        MinecraftServer.getServer().getPlayerList().sendAll(packetPlayOutPlayerInfo);
    }

    @Override
    public void addExecute(Player player) {
        PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, (((CraftPlayer) player).getHandle()));
        MinecraftServer.getServer().getPlayerList().sendAll(packetPlayOutPlayerInfo);
    }

    @Override
    public void updateTablist() {
        /*this.getOnlinePlayers().forEach(this::removeExecute);
        this.getOnlinePlayers().stream()
                .map(player -> CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId()))
                .sorted(Comparator.comparingInt(potPlayer -> +potPlayer.getActiveGrant().getRank().getWeight()))
                .forEach(potPlayer -> this.addExecute(potPlayer.getPlayer()));*/
    }

    private Collection<Player> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> !CorePlugin.getInstance().getPlayerManager().getPlayer(player).isVanished())
                .filter(player -> !CorePlugin.getInstance().getPlayerManager().getPlayer(player).isStaffMode())
                .collect(Collectors.toList());
    }
}
