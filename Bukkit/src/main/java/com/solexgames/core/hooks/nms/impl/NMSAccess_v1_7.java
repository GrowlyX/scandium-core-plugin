package com.solexgames.core.hooks.nms.impl;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.hooks.nms.INMS;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NMSAccess_v1_7 implements INMS {

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
        final List<EntityPlayer> playerList = new ArrayList<>(net.minecraft.server.v1_7_R4.MinecraftServer.getServer().getPlayerList().players);
        final List<EntityPlayer> finalList = playerList.stream()
                .sorted(Comparator.comparingInt(potPlayer -> +(CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getName()).getDisguiseRank() != null ? CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getName()).getDisguiseRank().getWeight() : CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getName()).getActiveGrant().getRank().getWeight())))
                .collect(Collectors.toList());

        try {
            Object list = net.minecraft.server.v1_7_R4.MinecraftServer.getServer().getPlayerList().getClass()
                    .getMethod("playerList", ((Class<?>[]) null))
                    .invoke(net.minecraft.server.v1_7_R4.MinecraftServer.getServer().getPlayerList());
            Class<?> playerListClass = list.getClass().getSuperclass();
            Field declaredField = playerListClass.getDeclaredField("players");

            declaredField.set(list, finalList);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void setupTablist(Player player) {
        // TODO: Convert 1.8 NMS to 1.7 for 1.7 Tablist support
    }

    @Override
    public void updatePlayer(Player player) {
        final net.minecraft.server.v1_7_R4.EntityPlayer entityPlayer = ((org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer) player).getHandle();
        final Location previousLocation = player.getLocation().clone();

        PacketPlayOutPlayerInfo.removePlayer(entityPlayer);
        PacketPlayOutPlayerInfo.addPlayer(entityPlayer);

        entityPlayer.playerConnection.sendPacket(new net.minecraft.server.v1_7_R4.PacketPlayOutRespawn(
                0,
                entityPlayer.getWorld().difficulty,
                entityPlayer.getWorld().worldData.getType(),
                EnumGamemode.valueOf(entityPlayer.getBukkitEntity().getGameMode().name())
        ));

        player.getInventory().setItemInHand(player.getItemInHand());
        player.updateInventory();

        player.teleport(previousLocation);
    }

    @Override
    public void updateCache(Player player) {
        final List<net.minecraft.server.v1_7_R4.EntityPlayer> playerList = new ArrayList<>(net.minecraft.server.v1_7_R4.MinecraftServer.getServer().getPlayerList().players);
        final net.minecraft.server.v1_7_R4.EntityPlayer entityPlayer = playerList.stream()
                .filter(entityPlayer1 -> entityPlayer1.getUniqueID().equals(player.getUniqueId()))
                .findFirst().orElse(null);

        playerList.remove(entityPlayer);
        playerList.add(((org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer) player).getHandle());

        try {
            final Object list = net.minecraft.server.v1_7_R4.MinecraftServer.getServer().getPlayerList().getClass()
                    .getMethod("playerList", ((Class<?>[]) null))
                    .invoke(net.minecraft.server.v1_7_R4.MinecraftServer.getServer().getPlayerList());
            final Class<?> playerListClass = list.getClass().getSuperclass();
            final Field declaredField = playerListClass.getDeclaredField("players");

            declaredField.set(list, playerList);
        } catch (Exception ignored) {
        }
    }
}
