package com.solexgames.core.internal.tablist.playerversion.impl;

import com.solexgames.core.internal.tablist.playerversion.IPlayerVersion;
import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;

public class PlayerVersionProtocolSupport implements IPlayerVersion {

    @Override
    public int getProtocolVersion(Player player) {
        return ProtocolSupportAPI.getProtocolVersion(player).getId();
    }
}
