package vip.potclub.core.lunar;

import com.lunarclient.bukkitapi.LunarClientAPI;
import org.bukkit.command.CommandExecutor;
import vip.potclub.core.CorePlugin;

public abstract class AbstractClientInjector implements CommandExecutor {

    protected LunarClientAPI lunarClient;
    protected CorePlugin instance;

    public AbstractClientInjector() {
        this.lunarClient = LunarClientAPI.getInstance();
        this.instance = CorePlugin.getInstance();
    }
}
