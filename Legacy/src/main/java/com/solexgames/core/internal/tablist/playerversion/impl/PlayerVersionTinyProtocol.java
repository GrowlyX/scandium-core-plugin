package com.solexgames.core.internal.tablist.playerversion.impl;

import com.solexgames.core.internal.tablist.OutlastTab;
import com.solexgames.core.internal.tablist.playerversion.IPlayerVersion;
import org.bukkit.entity.Player;

public class PlayerVersionTinyProtocol implements IPlayerVersion {

    @Override
    public int getProtocolVersion(Player player) {
        return OutlastTab.getInstance().getTinyProtocol().getProtocolVersion(player);
    }
}
