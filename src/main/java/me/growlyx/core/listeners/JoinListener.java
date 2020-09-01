package me.growlyx.core.listeners;

import me.growlyx.core.Core;
import me.growlyx.core.utils.CC;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent join) {

        for (String string: Core.instance.m.getConfig().getStringList("JOIN.JOIN-MESSAGE")) {
            join.getPlayer().sendMessage(CC.translate(string));
            CC.translate("dh");
        }

    }
}
