package com.solexgames.core.abstraction.protocol;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.solexgames.core.CorePlugin;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public abstract class AbstractChatInterceptor implements IChatInterceptor {

    protected ProtocolManager protocolManager;
    protected FileConfiguration config;
    protected CorePlugin coreInstance;

    public AbstractChatInterceptor() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.config = CorePlugin.getInstance().getConfig();
        this.coreInstance = CorePlugin.getInstance();
    }

    public abstract void initializePacketInterceptor();

}
