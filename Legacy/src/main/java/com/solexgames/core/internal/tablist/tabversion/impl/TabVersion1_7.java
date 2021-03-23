package com.solexgames.core.internal.tablist.tabversion.impl;

import com.solexgames.core.internal.tablist.OutlastTab;
import com.solexgames.core.internal.tablist.Tablist;
import com.solexgames.core.internal.tablist.tabversion.ITabVersion;
import com.solexgames.core.internal.tablist.util.TabUtil;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.spigotmc.ProtocolInjector;

import java.util.UUID;

public class TabVersion1_7 implements ITabVersion {

    private MinecraftServer minecraftServer = MinecraftServer.getServer();
    private WorldServer worldServer = minecraftServer.getWorldServer(0);
    private PlayerInteractManager playerInteractManager = new PlayerInteractManager(worldServer);
    private OutlastTab outlastTab;

    public TabVersion1_7(OutlastTab outlastTab) {
        this.outlastTab = outlastTab;
    }

    public void setup(Player player) {
        removePlayerInfoForEveryone(player);
        for(Player online : Bukkit.getOnlinePlayers())removePlayerInfo(player, ((CraftPlayer) online).getHandle());

        if(outlastTab.getTablist().getElements(player) == null || outlastTab.getTablist().getElements(player).isEmpty()) return;

        Scoreboard scoreboard = player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard() ?
                Bukkit.getScoreboardManager().getNewScoreboard() : player.getScoreboard();

                for(Player online : Bukkit.getOnlinePlayers())removePlayerInfo(player, ((CraftPlayer) online).getHandle());
        removePlayerInfoForEveryone(player);

        outlastTab.getTablists().add(new Tablist(scoreboard, player, outlastTab));
    }

    public Object createPlayer(Player player, String name) {
        OfflinePlayer testPlayer = TabUtil.createNewFakePlayer(UUID.randomUUID(), name);
        GameProfile gameProfileTest = new GameProfile(testPlayer.getUniqueId(), testPlayer.getName());
        EntityPlayer epTest = new EntityPlayer(minecraftServer, worldServer, gameProfileTest, playerInteractManager);
        gameProfileTest.getProperties().put("textures",
                new Property("textures", "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0="
                        , "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw="));

        epTest.ping = 1;
        addPlayerInfo(player, epTest);

        return epTest;
    }

    public void addPlayerInfo(Player player, Object ep) {
        EntityPlayer entityPlayer = (EntityPlayer) ep;
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo().addPlayer(entityPlayer);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void removePlayerInfo(Player player, Object ep) {
        EntityPlayer entityPlayer = (EntityPlayer) ep;
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo().removePlayer(entityPlayer);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void removePlayerInfoForEveryone(Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        for(Player online : Bukkit.getOnlinePlayers())removePlayerInfo(online, ep);
    }

    @Override
    public void update(Player player) {
        if(OutlastTab.getInstance().getTablist().getElements(player) == null || OutlastTab.getInstance().getTablist().getElements(player).isEmpty())
            OutlastTab.getInstance().getTablists().stream().filter(tablist -> tablist.getPlayer() == player).findFirst().orElse(null).disable();
        else
            OutlastTab.getInstance().getTablists().stream().filter(tablist -> tablist.getPlayer() == player).findFirst().orElse(null).enable();

        if (outlastTab.getTablist().getHeader(player) != null && outlastTab.getTablist().getFooter(player) != null)
            setHeaderAndFooter(player);
    }

    @Override
    public void setHeaderAndFooter(Player player) {
        if(getSlots(player) == 80) {
            IChatBaseComponent componentHeader = ChatSerializer.a("{\"text\": \"" +
                    ChatColor.translateAlternateColorCodes('&', outlastTab.getTablist().getHeader(player) + "\"}"));
            IChatBaseComponent componentFooter = ChatSerializer.a("{\"text\": \"" +
                    ChatColor.translateAlternateColorCodes('&', outlastTab.getTablist().getFooter(player) + "\"}"));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new ProtocolInjector.PacketTabHeader(
                    componentHeader,componentFooter));
        }
    }

    @Override
    public void addAllOnlinePlayers(Player player) {
    	for(Player online : Bukkit.getOnlinePlayers())addPlayerInfo(player, ((CraftPlayer) online).getHandle());
    }

    @Override
    public void removeAllOnlinePlayers(Player player) {
    	for(Player online : Bukkit.getOnlinePlayers())removePlayerInfo(player, ((CraftPlayer) online).getHandle());
    }

    @Override
    public int getSlots(Player player) {
        return outlastTab.getPlayerVersion().getProtocolVersion(player) >= 47 ? 80 : 60;
    }
}
