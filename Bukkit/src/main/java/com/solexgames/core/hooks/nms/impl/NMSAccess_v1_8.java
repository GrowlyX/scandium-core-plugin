package com.solexgames.core.hooks.nms.impl;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.hooks.nms.INMS;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NMSAccess_v1_8 implements INMS {

    @Override
    public void removeExecute(Player player) {
        final PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, (((CraftPlayer) player).getHandle()));
        MinecraftServer.getServer().getPlayerList().sendAll(packetPlayOutPlayerInfo);
    }

    @Override
    public void addExecute(Player player) {
        final PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, (((CraftPlayer) player).getHandle()));
        MinecraftServer.getServer().getPlayerList().sendAll(packetPlayOutPlayerInfo);
    }

    @Override
    public void updateTablist() {
        final List<EntityPlayer> playerList = new ArrayList<>(net.minecraft.server.v1_8_R3.MinecraftServer.getServer().getPlayerList().players);
        final List<EntityPlayer> finalList = playerList.stream()
                .sorted(Comparator.comparingInt(potPlayer -> -(CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getName()).getDisguiseRank() != null ? CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getName()).getDisguiseRank().getWeight() : CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getName()).getActiveGrant().getRank().getWeight())))
                .collect(Collectors.toList());

        try {
            final Object list = net.minecraft.server.v1_8_R3.MinecraftServer.getServer().getPlayerList().getClass()
                    .getMethod("playerList", ((Class<?>[]) null))
                    .invoke(net.minecraft.server.v1_8_R3.MinecraftServer.getServer().getPlayerList());
            final Class<?> playerListClass = list.getClass().getSuperclass();
            final Field declaredField = playerListClass.getDeclaredField("players");

            declaredField.set(list, finalList);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void setupTablist(Player player) {
        if (CorePlugin.getInstance().getServerSettings().isTabEnabled()) {
            final IChatBaseComponent tabHeader = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CorePlugin.getInstance().getServerSettings().getTabHeader() + "\"}");
            final IChatBaseComponent tabFooter = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CorePlugin.getInstance().getServerSettings().getTabFooter() + "\"}");
            final PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

            try {
                final Field headerField = packet.getClass().getDeclaredField("a");

                headerField.setAccessible(true);
                headerField.set(packet, tabHeader);
                headerField.setAccessible(false);

                final Field footerField = packet.getClass().getDeclaredField("b");

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
        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        final Location previousLocation = player.getLocation().clone();

        entityPlayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutRespawn(
                entityPlayer.getWorld().worldProvider.getDimension(),
                entityPlayer.getWorld().worldData.getDifficulty(),
                entityPlayer.getWorld().worldData.getType(),
                WorldSettings.EnumGamemode.valueOf(entityPlayer.getBukkitEntity().getGameMode().name())
        ));

        player.updateInventory();
        player.setGameMode(player.getGameMode());

        final PlayerInventory inventory = player.getInventory();
        inventory.setHeldItemSlot(inventory.getHeldItemSlot());

        final double oldHealth = player.getHealth();
        final int oldFood = player.getFoodLevel();
        final float oldSat = player.getSaturation();

        player.setFoodLevel(20);
        player.setFoodLevel(oldFood);
        player.setSaturation(5.0F);
        player.setSaturation(oldSat);
        player.setMaxHealth(player.getMaxHealth());
        player.setHealth(20.0D);
        player.setHealth(oldHealth);

        final float experience = player.getExp();
        final int totalExperience = player.getTotalExperience();

        player.setExp(experience);
        player.setTotalExperience(totalExperience);
        player.setWalkSpeed(player.getWalkSpeed());

        player.teleport(previousLocation);
    }

    @Override
    public void updateCache(Player player) {
        final List<EntityPlayer> playerList = new ArrayList<>(net.minecraft.server.v1_8_R3.MinecraftServer.getServer().getPlayerList().players);
        final EntityPlayer entityPlayer = playerList.stream()
                .filter(entityPlayer1 -> entityPlayer1.getUniqueID().equals(player.getUniqueId()))
                .findFirst().orElse(null);

        playerList.remove(entityPlayer);
        playerList.add(((CraftPlayer) player).getHandle());

        try {
            final Object list = net.minecraft.server.v1_8_R3.MinecraftServer.getServer().getPlayerList().getClass()
                    .getMethod("playerList", ((Class<?>[]) null))
                    .invoke(net.minecraft.server.v1_8_R3.MinecraftServer.getServer().getPlayerList());
            final Class<?> playerListClass = list.getClass().getSuperclass();
            final Field declaredField = playerListClass.getDeclaredField("players");

            declaredField.set(list, playerList);
        } catch (Exception ignored) {
        }
    }
}
