package vip.potclub.core.protocol;

import com.google.common.base.Splitter;
import org.bukkit.entity.Player;

public abstract class TabInterceptor {

    public boolean isCompletionCancelled(Player player, String completedCommand) {
        if (player.hasPermission("example.exempt"))
            return false;
        if (completedCommand == null)
            return false;

        for (String complete : Splitter.on((char)0).split(completedCommand)) {
            if (complete.startsWith("/") && !complete.contains(" ")) {
                return true;
            }
        }
        return false;
    }

    public abstract void close();
}
