package com.solexgames.xenon.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.solexgames.xenon.util.Color;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@CommandAlias("glist|globallist")
public class ListCommand extends BaseCommand {

    @Default
    public void onList(ProxiedPlayer proxiedPlayer) {
        proxiedPlayer.sendMessage(Color.translate("&bThere " + (BungeeCord.getInstance().getPlayers().size() == 1 ? "is " : "are ") + "currently &3" + BungeeCord.getInstance().getPlayers().size() + " " + (BungeeCord.getInstance().getPlayers().size() == 1 ? "Player " : "Players ") + "&bon the network."));
    }
}
