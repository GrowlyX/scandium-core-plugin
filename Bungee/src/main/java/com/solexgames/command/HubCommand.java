package com.solexgames.command;

import com.solexgames.CorePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Command;

public class HubCommand extends Command {

    private final CorePlugin plugin;

    public HubCommand(CorePlugin plugin) {
        super("hub", null, "lobby");
        this.plugin = plugin;
    }

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "This command is only for players."));
        } else {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (!player.getServer().getInfo().getName().contains("hub") && !player.getServer().getInfo().getName().contains("lobby")) {
                ServerInfo hub = this.plugin.getBestHub();
                if (hub != null) {
                    player.connect(hub, ServerConnectEvent.Reason.COMMAND);
                } else {
                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "There aren't any available hubs right now!"));
                }
            } else {
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You are currently in a hub."));
            }
        }
    }
}
