package com.solexgames.core.command.extend.network;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.server.NetworkServer;
import com.solexgames.core.util.Color;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class NetworkCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof ConsoleCommandSender) {
            System.out.println("no");
            return false;
        }

        Player player = (Player) commandSender;
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        if (player.hasPermission("scandium.command.network")) {
            if (args.length == 0) {
                if (!CorePlugin.getInstance().getServerManager().getNetworkServers().isEmpty()) {
                    player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                    player.sendMessage(Color.translate(network.getMainColor() + ChatColor.BOLD.toString() + "Network Data:"));
                    player.sendMessage(Color.translate("  "));
                    CorePlugin.getInstance().getServerManager().getNetworkServers().forEach(networkServer -> player.sendMessage(Color.translate(" &7- " + network.getSecondaryColor() + networkServer.getServerName().toLowerCase() +
                                    "&7(TPS: " + networkServer.getTicksPerSecondSimplified() + "&7)" +
                                    " (Online: " + networkServer.getOnlinePlayers() + "/" + networkServer.getMaxPlayerLimit() + "&7)" +
                                    " (Status: " + networkServer.getServerStatus().getServerStatusFancyString() + "&7)" +
                                    " (Type: " + networkServer.getServerType().getServerTypeString() + "&7)")));
                    player.sendMessage(Color.translate("  "));
                    player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                } else {
                    player.sendMessage(Color.translate("&cNo servers are currently online."));
                }
            }
            if (args.length > 0) {
                String server = args[0];
                NetworkServer networkServer = NetworkServer.getByName(server);

                if (networkServer != null) {
                    player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                    player.sendMessage(Color.translate(network.getMainColor() + ChatColor.BOLD.toString() + StringUtils.capitalize(networkServer.getServerName()).toLowerCase() + " Data:"));
                    player.sendMessage(Color.translate("  "));
                    player.sendMessage(Color.translate(network.getSecondaryColor() + "Server Type: &3" + network.getMainColor() + networkServer.getServerType().getServerTypeString()));
                    player.sendMessage(Color.translate(network.getSecondaryColor() + "Server Status: &3" + network.getMainColor() + networkServer.getServerStatus().getServerStatusFancyString()));
                    player.sendMessage(Color.translate(network.getSecondaryColor() + "Max Players: &3" + network.getMainColor() + networkServer.getMaxPlayerLimit()));
                    player.sendMessage(Color.translate(network.getSecondaryColor() + "Online Players: &3" +network.getMainColor() +  networkServer.getOnlinePlayers()));
                    player.sendMessage(Color.translate(network.getSecondaryColor() + "Ticks Per Second: &3" + network.getMainColor() + networkServer.getTicksPerSecond()));
                    player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                } else {
                    player.sendMessage(Color.translate("&cThat server is not online or does not exist."));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
