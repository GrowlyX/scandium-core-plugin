package com.solexgames.core.hook.access.extend;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.hook.access.AbstractNMSAccess;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
        final List<EntityPlayer> playerList = new ArrayList<>(MinecraftServer.getServer().getPlayerList().players);
        final List<EntityPlayer> finalList = playerList.stream()
                .sorted(Comparator.comparingInt(potPlayer -> -(CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getName()).getDisguiseRank() != null ? CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getName()).getDisguiseRank().getWeight() : CorePlugin.getInstance().getPlayerManager().getPlayer(potPlayer.getName()).getActiveGrant().getRank().getWeight())))
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
        if (CorePlugin.TAB_ENABLED) {
            IChatBaseComponent tabHeader = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CorePlugin.TAB_HEADER + "\"}");
            IChatBaseComponent tabFooter = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CorePlugin.TAB_FOOTER + "\"}");
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
}
