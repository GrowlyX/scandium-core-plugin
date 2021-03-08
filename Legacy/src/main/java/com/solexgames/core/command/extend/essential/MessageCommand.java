package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MessageCommand extends BaseCommand implements TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        if (args.length == 0) {
            player.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: /" + serverType.getMainColor() + label + ChatColor.WHITE + " <player> <message>."));
        }
        if (args.length > 0) {
            if (args.length == 1) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <player> <message>."));
            }
            if (args.length > 1) {
                Player target = Bukkit.getPlayerExact(args[0]);
                String message = StringUtil.buildMessage(args, 1);

                if (target == null) {
                    player.sendMessage(Color.translate("&cThat player does not exist."));
                    return false;
                }

                PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
                PotPlayer potTarget = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

                if (potTarget.isVanished()) {
                    player.sendMessage(Color.translate("&cThat player does not exist."));
                    return false;
                }
                if (potTarget.isIgnoring(potPlayer.getPlayer())) {
                    player.sendMessage(Color.translate("&cThat player is currently ignoring you."));
                    return false;
                }
                if (potPlayer.isIgnoring(potTarget.getPlayer())) {
                    player.sendMessage(Color.translate("&cYou are currently ignoring that player."));
                    return false;
                }
                if (!potPlayer.isCanReceiveDms()) {
                    player.sendMessage(Color.translate("&cYou have your dms disabled."));
                    return false;
                }
                if (!potTarget.isCanReceiveDms()) {
                    player.sendMessage(Color.translate("&cThat player has their dms disabled."));
                    return false;
                }
                if (CorePlugin.getInstance().getFilterManager().isDmFiltered(player, target.getName(), message)) {
                    player.sendMessage(Color.translate("&cYou cannot use censored words in a direct message."));
                    return false;
                }

                StringUtil.sendPrivateMessage(player, target, message);

                potPlayer.setLastRecipient(target);
                potTarget.setLastRecipient(player);
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return this.getOnlineString();
    }

    public List<String> getOnlineString() {
        List<PotPlayer> potPlayers = Bukkit.getOnlinePlayers().stream()
                .map(CorePlugin.getInstance().getPlayerManager()::getPlayer)
                .filter(Objects::nonNull)
                .filter(potPlayer -> !potPlayer.isVanished())
                .filter(potPlayer -> !potPlayer.isStaffMode())
                .collect(Collectors.toList());

        List<String> players = new ArrayList<>();
        potPlayers.forEach(potPlayer -> players.add(potPlayer.getPlayer().getName()));

        return players;
    }
}
