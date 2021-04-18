package com.solexgames.core.command.impl.network;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.server.NetworkServer;
import com.solexgames.core.util.Color;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;

public class NetworkCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("scandium.command.network")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            final String networkData = CorePlugin.getInstance().getServerManager().getNetworkServers().stream()
                    .map(this::getNetworkData)
                    .collect(Collectors.joining("\n"));

            sender.sendMessage(new String[]{
                    ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 53),
                    Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Network Data: ",
                    networkData,
                    ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 53),
            });
        } else {
            final String serverName = args[0];
            final NetworkServer server = NetworkServer.getByName(serverName);

            if (server == null) {
                sender.sendMessage(ChatColor.RED + "Error: No server with name \"" + serverName + "\" is online.");
                return false;
            }

            sender.sendMessage(new String[]{
                    ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 53),
                    Color.MAIN_COLOR + ChatColor.BOLD.toString() + server.getServerName() + " Data:",
                    "",
                    this.getIndividualNetworkData(server),
                    ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 53)
            });
        }

        return false;
    }

    /**
     * Get the data of the network
     *
     * @param server the server
     * @return the data in a formatted string
     */
    private String getNetworkData(NetworkServer server) {
        return ChatColor.GRAY + ChatColor.BOLD.toString() + " • " + String.join(" ", new String[]{
                Color.SECONDARY_COLOR + server.getServerName(),
                ChatColor.GRAY + "(TPS: " + server.getTicksPerSecond() + ")",
                ChatColor.GRAY + "(Online: " + server.getOnlinePlayers() + "/" + server.getMaxPlayerLimit() + ")",
                ChatColor.GRAY + "(State: " + server.getServerStatus().name() + ")",
                ChatColor.GRAY + "(Type: " + server.getServerType().name() + ")",
        });
    }

    /**
     * Get individual data of a {@link NetworkServer}
     *
     * @param server the server to get the data from
     * @return the formatted network data
     */
    private String getIndividualNetworkData(NetworkServer server) {
        return String.join("\n", new String[]{
                Color.SECONDARY_COLOR + "Server Type: " + Color.MAIN_COLOR + server.getServerType().getServerTypeString(),
                Color.SECONDARY_COLOR + "server Status: " + Color.translate(server.getServerStatus().getServerStatusFancyString()),
                Color.SECONDARY_COLOR + "Max Players: " + Color.MAIN_COLOR + server.getMaxPlayerLimit(),
                Color.SECONDARY_COLOR + "Online Players: " + Color.MAIN_COLOR + server.getOnlinePlayers(),
                Color.SECONDARY_COLOR + "Ticks per Second: " + Color.MAIN_COLOR + server.getTicksPerSecond(),
        });
    }
}
