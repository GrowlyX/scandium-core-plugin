package com.solexgames.core.nms.extend;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.nms.AbstractNMSImplementation;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class NMSImplementation_v1_7 extends AbstractNMSImplementation {

    @Override
    public void removeExecute(Player player) {
        MinecraftServer.getServer().getPlayerList().sendAll(PacketPlayOutPlayerInfo.removePlayer((((CraftPlayer) player).getHandle())));
    }

    @Override
    public void addExecute(Player player) {
        MinecraftServer.getServer().getPlayerList().sendAll(PacketPlayOutPlayerInfo.addPlayer((((CraftPlayer) player).getHandle())));
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
