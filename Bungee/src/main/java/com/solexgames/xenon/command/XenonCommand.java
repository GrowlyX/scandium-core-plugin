package com.solexgames.xenon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.solexgames.xenon.util.Color;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author GrowlyX
 * @since 3/5/2021
 */

@CommandAlias("xenon")
public class XenonCommand extends BaseCommand {

    @Default
    public void onXenon(ProxiedPlayer proxiedPlayer) {
        proxiedPlayer.sendMessages(
                Color.translate("&eThis proxy is running &6&lXenon&e."),
                Color.translate("&7&oCreated by SolexGames.")
        );
    }
}
