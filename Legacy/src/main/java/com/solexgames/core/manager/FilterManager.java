package com.solexgames.core.manager;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
public class FilterManager {

    private final CorePlugin plugin;
    private final List<String> filteredMessages;

    public FilterManager() {
        this.plugin = CorePlugin.getInstance();
        this.filteredMessages = this.plugin.getFilterConfig().getStringList("messages");
    }

    public boolean isMessageFiltered(Player player, String message) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        String fixedMessage = message.toLowerCase()
                .replaceAll("[^a-z0-9 ] ", " ")
                .replace("@ ", " a")
                .replace("3 ", " e")
                .replace("0 ", " o")
                .replace("4 ", " a")
                .replace("1 ", " i")
                .replace("5 ", " s");

        this.filteredMessages.stream()
                .filter(s -> fixedMessage.contains(s.toLowerCase()))
                .forEach(s -> {
                    if (!atomicBoolean.get()) {
                        if (!player.hasPermission("scandium.filter.bypass"))
                            handleAlert(player, fixedMessage);

                        atomicBoolean.set(true);
                    }
                });

        return atomicBoolean.get();
    }

    private void handleAlert(Player player, String message) {
        Bukkit.getOnlinePlayers()
                .stream()
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(PotPlayer::isCanSeeFiltered)
                .forEach(potPlayer -> {
                    PotPlayer targetPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
                    potPlayer.getPlayer().sendMessage(Color.translate("&c[Filtered] &e" + targetPlayer.getColorByRankColor() + targetPlayer.getName() + "&7: &e" + message));
                });
    }
}
