package com.solexgames.core.abstraction.lunar;

import com.lunarclient.bukkitapi.LunarClientAPI;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public abstract class AbstractLunar {

    protected LunarClientAPI lunarClientAPI;


    public abstract void sendNotification(Player player, String translate);
}
