package com.solexgames.core.hooks.protocol;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.solexgames.core.CorePlugin;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

@Getter
public abstract class AbstractPacketHandler {

    protected ProtocolManager protocolManager;
    protected FileConfiguration config;
    protected CorePlugin coreInstance;

    public AbstractPacketHandler() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.config = CorePlugin.getInstance().getConfig();
        this.coreInstance = CorePlugin.getInstance();
    }

    /**
     * Initializes the ProtocolLib packet system
     */
    public abstract void initializePacketHandlers();

    public abstract boolean sendDemoScreen(Player player);
    public abstract boolean turnPlayer(Player player);

}
