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
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        if (!sender.hasPermission("scandium.command.network")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        if (args.length == 0) {
            if (CorePlugin.getInstance().getServerManager().getNetworkServers().isEmpty()) {
                sender.sendMessage(ChatColor.RED + "No servers are currently online.");
                return false;
            }

            sender.sendMessage(Color.translate(network.getMainColor() + "&m" + StringUtils.repeat("-", 53)));
            sender.sendMessage(Color.translate(network.getMainColor() + ChatColor.BOLD.toString() + "ServerType Data:"));
            sender.sendMessage(Color.translate("  "));
            CorePlugin.getInstance().getServerManager().getNetworkServers().forEach(networkServer -> sender.sendMessage(Color.translate(" &7* " + network.getSecondaryColor() + networkServer.getServerName().toLowerCase() +
                    "&7(TPS: " + networkServer.getTicksPerSecondSimplified() + "&7)" +
                    " (Online: " + networkServer.getOnlinePlayers() + "/" + networkServer.getMaxPlayerLimit() + "&7)" +
                    " (Status: " + networkServer.getServerStatus().getServerStatusFancyString() + "&7)" +
                    " (Type: " + networkServer.getServerType().getServerTypeString() + "&7)")));
            sender.sendMessage(Color.translate("  "));
            sender.sendMessage(Color.translate(network.getMainColor() + "&m" + StringUtils.repeat("-", 53)));
        }
        if (args.length == 1) {
            NetworkServer networkServer = NetworkServer.getByName(args[0]);

            if (networkServer == null) {
                sender.sendMessage(ChatColor.RED + "&cThat server is not online or does not exist.");
                return false;
            }

            sender.sendMessage(Color.translate(network.getMainColor() + "&m" + StringUtils.repeat("-", 53)));
            sender.sendMessage(Color.translate(network.getMainColor() + ChatColor.BOLD.toString() + StringUtils.capitalize(networkServer.getServerName()).toLowerCase() + " Data:"));
            sender.sendMessage(Color.translate("  "));
            sender.sendMessage(Color.translate(network.getSecondaryColor() + "Server Type: &3" + network.getMainColor() + networkServer.getServerType().getServerTypeString()));
            sender.sendMessage(Color.translate(network.getSecondaryColor() + "Server Status: &3" + network.getMainColor() + networkServer.getServerStatus().getServerStatusFancyString()));
            sender.sendMessage(Color.translate(network.getSecondaryColor() + "Max Players: &3" + network.getMainColor() + networkServer.getMaxPlayerLimit()));
            sender.sendMessage(Color.translate(network.getSecondaryColor() + "Online Players: &3" +network.getMainColor() +  networkServer.getOnlinePlayers()));
            sender.sendMessage(Color.translate(network.getSecondaryColor() + "Ticks Per Second: &3" + network.getMainColor() + networkServer.getTicksPerSecond()));
            sender.sendMessage(Color.translate(network.getMainColor() + "&m" + StringUtils.repeat("-", 53)));
        }
        return false;
    }
}
