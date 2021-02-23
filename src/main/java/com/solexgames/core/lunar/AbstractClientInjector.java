package com.solexgames.core.lunar;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.solexgames.core.CorePlugin;
import org.bukkit.command.CommandExecutor;

public abstract class AbstractClientInjector implements CommandExecutor {

    protected LunarClientAPI lunarClient;
    protected CorePlugin instance;

    public AbstractClientInjector() {
        this.lunarClient = LunarClientAPI.getInstance();
        this.instance = CorePlugin.getInstance();
    }
}
