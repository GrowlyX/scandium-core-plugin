package com.solexgames.core.abstraction.lunar.extend;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCNotification;
import com.solexgames.core.abstraction.lunar.AbstractLunar;
import org.bukkit.entity.Player;

import java.time.Duration;

public class LunarImplementation extends AbstractLunar {

    public LunarImplementation() {
        super(LunarClientAPI.getInstance());
    }

    public void sendNotification(Player player, String message) {
        this.lunarClientAPI.sendNotification(player, new LCNotification(message, Duration.ofSeconds(10), LCNotification.Level.INFO));
    }

    public void enableModules(Player player) {
        this.lunarClientAPI.giveAllStaffModules(player);
    }

    public void disableModules(Player player) {
        this.lunarClientAPI.disableAllStaffModules(player);
    }
}
