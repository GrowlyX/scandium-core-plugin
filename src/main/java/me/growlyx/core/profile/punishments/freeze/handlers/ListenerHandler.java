package me.growlyx.core.profile.punishments.freeze.handlers;

import me.growlyx.core.Core;
import me.growlyx.core.profile.punishments.freeze.listeners.FreezeListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ListenerHandler
{
    private Core plugin;
    private Listener[] listeners;

    public ListenerHandler(final Core plugin) {
        this.listeners = new Listener[] { new FreezeListener(this) };
        this.plugin = plugin;
        this.loadListeners();
    }

    private void loadListeners() {
        Listener[] arrayOfListener;
        for (int j = (arrayOfListener = this.listeners).length, i = 0; i < j; ++i) {
            final Listener listener = arrayOfListener[i];
            this.plugin.getServer().getPluginManager().registerEvents(listener, (Plugin)this.plugin);
        }
    }

    public Core getPlugin() {
        return this.plugin;
    }
}
