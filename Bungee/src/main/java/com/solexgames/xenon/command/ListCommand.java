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
        proxiedPlayer.sendMessage(Color.translate("&eThere " + (BungeeCord.getInstance().getPlayers().size() == 1 ? "is " : "are ") + "currently &6" + BungeeCord.getInstance().getPlayers().size() + " " + (BungeeCord.getInstance().getPlayers().size() == 1 ? "Player " : "Players ") + "&eon the network."));
        proxiedPlayer.sendMessage(Color.translate("&7&oTo view all online players, use /rglist showall."));
    }
}
