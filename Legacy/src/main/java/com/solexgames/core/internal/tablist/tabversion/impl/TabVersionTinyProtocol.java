package com.solexgames.core.internal.tablist.tabversion.impl;

import com.solexgames.core.internal.tablist.OutlastTab;
import com.solexgames.core.internal.tablist.Tablist;
import com.solexgames.core.internal.tablist.tabversion.ITabVersion;
import com.solexgames.core.internal.tablist.util.TabUtil;
import com.solexgames.core.internal.tinyprotocol.Reflection;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.UUID;

public class TabVersionTinyProtocol implements ITabVersion {

    private Class<?> playerInfo = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo");
    private Class<?> playerInfoEnum = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
    private Class<?> headerFooter = Reflection.getMinecraftClass("PacketPlayOutPlayerListHeaderFooter");
    private Class<?> chatComponentText = Reflection.getMinecraftClass("ChatComponentText");
    private Class<?> entityPlayerClass = Reflection.getMinecraftClass("EntityPlayer");
    //private Class<?> playerInfoData = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo.PlayerInfoData");
    //private Class<?> gamemodeEnum = Reflection.getMinecraftClass("WorldSettings$EnumGamemode");
    private OutlastTab outlastTab;
    private Object server;
    private Object world;
    private Object interactManager;
    private int i = 0;

    public TabVersionTinyProtocol(OutlastTab outlastTab) {
        this.outlastTab = outlastTab;
        try {
            this.server = Reflection.getCraftBukkitClass("CraftServer").getMethod("getServer").invoke(Bukkit.getServer());
            this.world = Reflection.getCraftBukkitClass("CraftWorld").getMethod("getHandle").invoke(Bukkit.getServer().getWorlds().get(0));
            this.interactManager = Reflection.getMinecraftClass("PlayerInteractManager").getDeclaredConstructors()[0].newInstance(world);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setup(Player player) {
        if(outlastTab.getTablist().getElements(player) == null || outlastTab.getTablist().getElements(player).isEmpty()) return;
        Scoreboard scoreboard = player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard() ?
                Bukkit.getScoreboardManager().getNewScoreboard() : player.getScoreboard();

        outlastTab.getTablists().add(new Tablist(scoreboard, player, outlastTab));
    }

    @Override
    public void addPlayerInfo(Player player, Object ep) {
        try {
            Constructor<?> packetConstructor = playerInfo.getConstructors()[1];
            Object addPlayerEnum = playerInfoEnum.getField("ADD_PLAYER").get(null);
            Object array = Array.newInstance(entityPlayerClass, 1);

            Array.set(array, 0, ep);

            Object playerInfoPacket = packetConstructor.newInstance(addPlayerEnum, array);
            outlastTab.getTinyProtocol().sendPacket(player, playerInfoPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void sendData(Player player) {
        Bukkit.getOnlinePlayers().forEach(online -> {
            try {
                GameProfile gameProfile = (GameProfile) outlastTab.getTinyProtocol().getProfile.invoke(online, new Object[0]);
                Constructor constructor = playerInfoData.getConstructors()[0];
                Object notSet = gamemodeEnum.getField("NOT_SET").get(null);
                Object name = chatComponentText.getConstructors()[0].newInstance(online.getName());
                constructor.newInstance(gameProfile, 1, notSet, name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

     */

    @Override
    public Object createPlayer(Player player, String name) {
        try {
            OfflinePlayer testPlayer = TabUtil.createNewFakePlayer(UUID.randomUUID(), name);
            GameProfile gameProfileTest = new GameProfile(testPlayer.getUniqueId(), testPlayer.getName());

            Constructor<?> epConstructor = entityPlayerClass.getDeclaredConstructors()[0];

            Object epTest = epConstructor.newInstance(server, world, gameProfileTest, interactManager);
            //Source - https://mineskin.org/472988
            gameProfileTest.getProperties().put("textures",
                    new Property("textures", "eyJ0aW1lc3RhbXAiOjE1NDczMzg3OTQwODcsInByb2ZpbGVJZCI6IjFjZjQ0OTMwODY1MTQ4NGE5ZmZjODI5YjlmNDg3NGE2IiwicHJvZmlsZU5hbWUiOiJJbmdBbmciLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y1MGVlOGEwNDNlNzEwZTIyOGM5MjQ2NGQ4ZDJkYzgxZmE0MWJkZjRhZDczNThmNTBjYjk1MjQ3MzAzOTczZjUifX19"
                            , "mW3mZThLJuHHqMZjk8BO3WEXtqqh17/M4Px61qD8cdcs08QAN1y5dbGoqXubfZidtrTMBJPMuTCTLNKPpN9FPN7j7Y89NQKtPACwoQykugU53n32kqAklZQpQ7/3ehEiAYirnxk7hTS/nNrghpkYHpBWit/YdCEYpGkf2jsILqWCq8caHkW8frxOjuaoWvF2HJ8ocXQcnqjhg4BjRLNFfyrKneowAnFkES9O4l6psX2M7Pb1Gd59cIU3C1O6JhSmkW1mHN/Vpk4pGBZVkA8F6gn6m84KpxpdHeGTGIUh1VqRuyMwMXKPo/zVyFU/+AyWqTRYi/i3/Q6mDY9LV7HvxkGjPey5gSysuZvVzF3goaloa/pUPdXKiBKjuJzBujR4grlrsCWUL76dCj/j1eidYT+9SSMFjh2d1ttH4Y2fV+LW1JNKqhAPzQmUDdMKvaBkMj2WPjoiHdqhSCvMfGd0+rmH/KzJx/nYhhuif+DQy71FlROUDL0rc2aTA3EwPkxyXvLP66CXACdyPwgCcMxcGYjdWa58oiKXm5KRw7iCVxPfOw9mcmVphEqRbBsIU4ikJ3ow7/GaQBkBqloZR79L4YeSUWWy8QAH3xpSxMQkri1vaSL+k42TUbWvDJYG6TWRMFJ4xoUyWJQwPtvcXoWd02lCHQP0d+INYt6PPY8nAto="));
            addPlayerInfo(player, epTest);
            return epTest;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void removePlayerInfo(Player player, Object ep) {
        try {
            Constructor<?> packetConstructor = playerInfo.getConstructor(playerInfoEnum, Class.forName("[Lnet.minecraft.server." + Reflection.VERSION + ".EntityPlayer;"));
            Object addPlayerEnum = playerInfoEnum.getField("REMOVE_PLAYER").get(null);
            Object array = Array.newInstance(entityPlayerClass, 1);

            Array.set(array, 0, ep);

            Object playerInfoPacket = packetConstructor.newInstance(addPlayerEnum, array);
            outlastTab.getTinyProtocol().sendPacket(player, playerInfoPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removePlayerInfoForEveryone(Player player) {
        for(Player online : Bukkit.getOnlinePlayers())removePlayerInfo(online, outlastTab.getTinyProtocol().getPlayerHandle.invoke(player, new Object[0]));
    }

    @Override
    public void update(Player player) {
        //TODO: Find a better way for this
        if(outlastTab.getPlayerVersion().getProtocolVersion(player) < 47) {
        	for(Player online : Bukkit.getOnlinePlayers())addPlayerInfo(player, outlastTab.getTinyProtocol().getPlayerHandle.invoke(online));

            new BukkitRunnable() {
                @Override
                public void run() {
                    removeAllOnlinePlayers(player);
                }
            }.runTaskLater(outlastTab.getJavaPlugin(), 1L);
        }

        if(OutlastTab.getInstance().getTablist().getElements(player) == null || OutlastTab.getInstance().getTablist().getElements(player).isEmpty())
            OutlastTab.getInstance().getTablists().stream().filter(tablist -> tablist.getPlayer() == player).findFirst().orElse(null).disable();
        else
            OutlastTab.getInstance().getTablists().stream().filter(tablist -> tablist.getPlayer() == player).findFirst().orElse(null).enable();

        if (outlastTab.getTablist().getHeader(player) != null && outlastTab.getTablist().getFooter(player) != null)
            setHeaderAndFooter(player);
    }

    @Override
    public void setHeaderAndFooter(Player player){
        if(getSlots(player) == 80) {
            try {
                if(Reflection.VERSION.contains("v1_13") || Reflection.VERSION.contains("v1_14")) {
                    player.getClass().getMethod("setPlayerListHeader", String.class).invoke(player,
                            ChatColor.translateAlternateColorCodes('&', outlastTab.getTablist().getHeader(player)));
                    player.getClass().getMethod("setPlayerListFooter", String.class).invoke(player,
                            ChatColor.translateAlternateColorCodes('&', outlastTab.getTablist().getFooter(player)));
                    return;
                }
                    Object headerFooterPacket = headerFooter.newInstance();
                    Field a = headerFooterPacket.getClass().getDeclaredField("a");
                    Field b = headerFooterPacket.getClass().getDeclaredField("b");

                    a.setAccessible(true);
                    b.setAccessible(true);

                    Object chatComponentHeader = chatComponentText.getConstructors()[0].newInstance(ChatColor.
                            translateAlternateColorCodes('&', outlastTab.getTablist().getHeader(player)));
                    Object chatComponentFooter = chatComponentText.getConstructors()[0].newInstance(ChatColor.
                            translateAlternateColorCodes('&', outlastTab.getTablist().getFooter(player)));

                    a.set(headerFooterPacket, chatComponentHeader);
                    b.set(headerFooterPacket, chatComponentFooter);

                    outlastTab.getTinyProtocol().sendPacket(player, headerFooterPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addAllOnlinePlayers(Player player) {
    	for(Player online : Bukkit.getOnlinePlayers())addPlayerInfo(player, outlastTab.getTinyProtocol().getPlayerHandle.invoke(online, new Object[0]));
    }

    @Override
    public void removeAllOnlinePlayers(Player player) {
    	for(Player online : Bukkit.getOnlinePlayers())removePlayerInfo(player, outlastTab.getTinyProtocol().getPlayerHandle.invoke(online, new Object[0]));
    }

    @Override
    public int getSlots(Player player) {
        return outlastTab.getPlayerVersion().getProtocolVersion(player) >= 47 ? 80 : 60;
    }
}
