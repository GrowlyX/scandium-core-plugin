package com.solexgames.core.task;

import com.solexgames.core.util.external.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author GrowlyX
 * @since 8/9/2021
 */

public class MenuAutoUpdateThread extends Thread {

    @Override
    public void run() {
        Menu.getCurrentlyOpenedMenus().forEach((username, menu) -> {
            final Player player = Bukkit.getPlayer(username);

            if (player != null && menu.isAutoUpdate()) {
                menu.openMenu(player);
            }
        });

        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
