package com.solexgames.core.nms.extend;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.nms.AbstractNMSImplementation;
import com.solexgames.core.util.Color;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class NMSImplementation_v1_8 extends AbstractNMSImplementation {

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
