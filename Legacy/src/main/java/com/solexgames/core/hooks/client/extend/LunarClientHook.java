package com.solexgames.core.hooks.client.extend;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCNotification;
import com.solexgames.core.hooks.client.AbstractClientHook;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Set;

public class LunarClientHook extends AbstractClientHook {

    @Override
    public void enableStaffModules(Player player) {
        LunarClientAPI.getInstance().giveAllStaffModules(player);
    }

    @Override
    public void disableStaffModules(Player player) {
        LunarClientAPI.getInstance().disableAllStaffModules(player);
    }

    @Override
    public void sendNotification(Player player, String message) {
        LunarClientAPI.getInstance().sendNotification(player, new LCNotification(message, Duration.ofSeconds(5), LCNotification.Level.INFO));
    }

    @Override
    public Set<Player> getOnlineLunarPlayers() {
        return LunarClientAPI.getInstance().getPlayersRunningLunarClient();
    }
}
