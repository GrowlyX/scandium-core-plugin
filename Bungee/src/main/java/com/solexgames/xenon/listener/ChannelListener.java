package com.solexgames.xenon.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Arrays;

public class ChannelListener implements Listener {

    @EventHandler
    public void onMessage(PluginMessageEvent event) {
        if (event.getTag().toLowerCase().equals("core:permissions")) {
            final DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(event.getData()));

            try {
                final String channel = dataInputStream.readUTF();

                if (!channel.equals("core:permissions")) {
                    return;
                }

                final String namePlayer = dataInputStream.readUTF();
                final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(namePlayer);
                final String fancyName = dataInputStream.readUTF();
                final String permsNotSplit = dataInputStream.readUTF();
                final String[] permissions = permsNotSplit.equals("NONE") ? new String[]{} : permsNotSplit.split(":");

                if (player != null) {
                    for (String permission : permissions) {
                        player.setPermission(permission, true);
                    }

                    player.setDisplayName(fancyName);
                }
            } catch (Exception exception) {
                System.out.println("[Messenger] Something went wrong while trying to parse a message from " + event.getTag() + "!");
                System.out.println("[Messenger] Reason: " + exception.getMessage());
                System.out.println("[Messenger] Channel: " + event.getTag());
            }
        }
    }
}
