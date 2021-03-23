package com.solexgames.core.internal.tablist.playerversion.impl;

import com.solexgames.core.internal.tablist.playerversion.IPlayerVersion;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.Via;

public class PlayerVersionViaVersion implements IPlayerVersion {

    @Override
    public int getProtocolVersion(Player player) {
        return Via.getAPI().getPlayerVersion(player.getUniqueId());
    }
}
