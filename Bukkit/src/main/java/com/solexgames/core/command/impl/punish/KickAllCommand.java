package com.solexgames.core.command.impl.punish;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.enums.NetworkServerType;
import com.solexgames.core.server.NetworkServer;
import com.solexgames.core.util.BungeeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(label = "kickall", permission = "scandium.command.kickall")
public class KickAllCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        final NetworkServer server = CorePlugin.getInstance().getServerManager().getNetworkServers().stream()
                .filter(networkServer -> networkServer.getServerType().equals(NetworkServerType.HUB))
                .findFirst().orElse(null);

        if (server == null) {
            sender.sendMessage(ChatColor.RED + "I'm sorry, but I couldn't find an available hub server to send the players to.");
            return false;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (sender instanceof Player && sender.getName().equals(player.getName())) {
                continue;
            }

            player.sendMessage(new String[]{
                    ChatColor.RED + "You've been disconnected from this server due to a staff member kicking all online players.",
                    ChatColor.GRAY + "You're now being connected to " + server.getServerName() + "..."
            });

            BungeeUtil.sendToServer(player, server.getServerName(), CorePlugin.getInstance());
        }

        return false;
    }
}
