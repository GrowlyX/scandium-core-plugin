package com.solexgames.core.hooks.nms.impl;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.hooks.nms.INMS;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NMSAccess_v1_12 implements INMS {

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
        final List<EntityPlayer> playerList = new ArrayList<>(net.minecraft.server.v1_12_R1.MinecraftServer.getServer().getPlayerList().players);
        final List<EntityPlayer> finalList = playerList.stream()
                .sorted(Comparator.comparingInt(potPlayer -> +(CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getName()).getDisguiseRank() != null ? CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getName()).getDisguiseRank().getWeight() : CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getName()).getActiveGrant().getRank().getWeight())))
                .collect(Collectors.toList());

        try {
            Object list = net.minecraft.server.v1_12_R1.MinecraftServer.getServer().getPlayerList().getClass()
                    .getMethod("playerList", ((Class<?>[]) null))
                    .invoke(net.minecraft.server.v1_12_R1.MinecraftServer.getServer().getPlayerList());
            Class<?> playerListClass = list.getClass().getSuperclass();
            Field declaredField = playerListClass.getDeclaredField("players");

            declaredField.set(list, finalList);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void setupTablist(Player player) {
        if (CorePlugin.getInstance().getServerSettings().isTabEnabled()) {
            IChatBaseComponent tabHeader = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CorePlugin.getInstance().getServerSettings().getTabHeader() + "\"}");
            IChatBaseComponent tabFooter = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CorePlugin.getInstance().getServerSettings().getTabFooter() + "\"}");
            PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

            try {
                Field headerField = packet.getClass().getDeclaredField("a");
                headerField.setAccessible(true);
                headerField.set(packet, tabHeader);
                headerField.setAccessible(false);

                Field footerField = packet.getClass().getDeclaredField("b");
                footerField.setAccessible(true);
                footerField.set(packet, tabFooter);
                footerField.setAccessible(false);
            } catch (Exception ignored) {
            }

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void updatePlayer(Player player) {
        final net.minecraft.server.v1_12_R1.EntityPlayer entityPlayer = ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) player).getHandle();
        final Location previousLocation = player.getLocation().clone();

        entityPlayer.playerConnection.sendPacket(new net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo(net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
        entityPlayer.playerConnection.sendPacket(new net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo(net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        entityPlayer.playerConnection.sendPacket(new net.minecraft.server.v1_12_R1.PacketPlayOutRespawn(
                entityPlayer.getWorld().worldProvider.getDimensionManager().getDimensionID(),
                entityPlayer.getWorld().worldData.getDifficulty(),
                entityPlayer.getWorld().worldData.getType(),
                EnumGamemode.valueOf(entityPlayer.getBukkitEntity().getGameMode().name())
        ));

        player.getInventory().setItemInHand(player.getItemInHand());
        player.updateInventory();

        player.teleport(previousLocation);
    }
}
