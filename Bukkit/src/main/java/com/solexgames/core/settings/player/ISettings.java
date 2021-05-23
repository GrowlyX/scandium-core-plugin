package com.solexgames.core.settings.player;

import com.solexgames.core.util.external.Button;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GrowlyX
 * @since 5/22/2021
 */

public interface ISettings {

    List<Button> getButtons(Player player);

}
