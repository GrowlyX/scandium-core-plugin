package vip.potclub.core.protocol;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import vip.potclub.core.CorePlugin;

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
}
