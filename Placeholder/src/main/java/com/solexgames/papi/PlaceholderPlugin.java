package com.solexgames.papi;

import com.solexgames.papi.extension.CoreExtension;
import com.solexgames.papi.listener.ServerListener;
import com.solexgames.papi.manager.ExtensionManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class PlaceholderPlugin extends JavaPlugin {

    @Getter
    private static PlaceholderPlugin instance;

    private ExtensionManager extensionManager;

    @Override
    public void onEnable() {
        instance = this;

        final CoreExtension coreExtension = new CoreExtension();

        if (coreExtension.canRegister()) {
            coreExtension.register();
        }

        this.extensionManager = new ExtensionManager();

        this.getServer().getPluginManager().registerEvents(new ServerListener(), this);
    }
}
