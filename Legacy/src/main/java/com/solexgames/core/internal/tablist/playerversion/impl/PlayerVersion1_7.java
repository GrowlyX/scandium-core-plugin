package com.solexgames.core.internal.tablist.playerversion.impl;

import com.solexgames.core.internal.tablist.playerversion.IPlayerVersion;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PlayerVersion1_7 implements IPlayerVersion {

    @Override
    public int getProtocolVersion(Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion();
    }
}
