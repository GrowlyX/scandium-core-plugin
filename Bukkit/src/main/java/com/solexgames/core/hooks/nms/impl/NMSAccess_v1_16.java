package com.solexgames.core.hooks.nms.impl;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.hooks.nms.INMS;
import net.minecraft.server.v1_16_R3.EnumGamemode;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NMSAccess_v1_16 implements INMS {

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
        final List<EntityPlayer> playerList = new ArrayList<>(MinecraftServer.getServer().getPlayerList().players);
        final List<EntityPlayer> finalList = playerList.stream()
                .sorted(Comparator.comparingInt(potPlayer -> +(CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getName()).getDisguiseRank() != null ? CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getName()).getDisguiseRank().getWeight() : CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getName()).getActiveGrant().getRank().getWeight())))
                .collect(Collectors.toList());

        try {
            Object list = MinecraftServer.getServer().getPlayerList().getClass()
                    .getMethod("playerList", ((Class<?>[]) null))
                    .invoke(MinecraftServer.getServer().getPlayerList());
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
        final net.minecraft.server.v1_16_R3.EntityPlayer entityPlayer = ((org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer) player).getHandle();
        final Location previousLocation = player.getLocation().clone();

        entityPlayer.playerConnection.sendPacket(new net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo(net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
        entityPlayer.playerConnection.sendPacket(new net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo(net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        /*entityPlayer.playerConnection.sendPacket(new net.minecraft.server.v1_16_R3.PacketPlayOutRespawn(
                entityPlayer.getWorld().getMinecraftWorld().getDimensionManager(),
                entityPlayer.getWorld().worldData.getDifficulty(),
                3L,
                entityPlayer.getWorldServer().getSeed(),
                entityPlayer.playerInteractManager.getGameMode(),
                entityPlayer.playerInteractManager.getGameMode(),
                false,
                false,
                true
        ));*/

        player.getInventory().setItemInHand(player.getItemInHand());
        player.updateInventory();

        player.teleport(previousLocation);
    }
}
