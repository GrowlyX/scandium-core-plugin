package com.solexgames.core.internal.tablist.playerversion.impl;

import com.comphenix.protocol.ProtocolLibrary;
import com.solexgames.core.internal.tablist.playerversion.IPlayerVersion;
import org.bukkit.entity.Player;

public class PlayerVersionProtocolLib implements IPlayerVersion {

    @Override
    public int getProtocolVersion(Player player) {
        return ProtocolLibrary.getProtocolManager().getProtocolVersion(player);
    }
}
