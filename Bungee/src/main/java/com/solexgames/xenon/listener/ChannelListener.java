package com.solexgames.xenon.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class ChannelListener implements Listener {

    @EventHandler
    public void onMessage(PluginMessageEvent event) {
        try {
            switch (event.getTag().toLowerCase()) {
                case "core-permissions":
                    final DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(event.getData()));
                    final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(dataInputStream.readUTF());

                    final String[] permissions = dataInputStream.readUTF().split(":");

                    if (player != null) {
                        for (String permission : permissions) {
                            player.setPermission(permission, true);
                        }
                    }
                    break;
                case "core-update":
                    break;
                default:
                    System.out.println("[Messenger] Could not identify the tag \"" + event.getTag() + "\"!");
                    break;
            }
        } catch (Exception exception) {
            System.out.println("[Messenger] Something went wrong while trying to parse a message from " + event.getTag() + "!");
            System.out.println("[Messenger] Reason: " + exception.getMessage());
            System.out.println("[Messenger] Channel: " + event.getTag());
        }
    }
}
