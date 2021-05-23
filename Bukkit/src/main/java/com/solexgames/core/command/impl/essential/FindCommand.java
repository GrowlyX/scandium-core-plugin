package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.global.NetworkPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.clickable.Clickable;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Command(label = "find", aliases = "search", permission = "scandium.command.find")
public class FindCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player>");
        }

        if (args.length > 0) {
            final String target = args[0];
            final NetworkPlayer networkPlayer = CorePlugin.getInstance().getPlayerManager().getNetworkPlayer(target);

            sender.sendMessage(Color.SECONDARY_COLOR + "Searching for that player...");

            if (networkPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Error: The player with the specified name does not exist.");
            } else {
                final Rank rank = Rank.getByName(networkPlayer.getRankName());
                final String displayName = Color.translate((rank != null ? rank.getColor() + rank.getItalic() : ChatColor.GRAY) + networkPlayer.getName());

                if (sender instanceof Player && sender.hasPermission("scandium.staff")) {
                    final Player player = (Player) sender;
                    final Clickable clickable = new Clickable("");

                    clickable.add(Color.MAIN_COLOR + displayName + Color.SECONDARY_COLOR + " is currently online " + Color.MAIN_COLOR + networkPlayer.getServerName() + Color.SECONDARY_COLOR + "!", Color.SECONDARY_COLOR + "Click to jump to " + ChatColor.GREEN + networkPlayer.getServerName() + Color.SECONDARY_COLOR + "!", "/jump " + networkPlayer.getServerName(), ClickEvent.Action.RUN_COMMAND);

                    player.spigot().sendMessage(clickable.asComponents());
                } else {
                    sender.sendMessage(Color.MAIN_COLOR + displayName + Color.SECONDARY_COLOR + " is currently online " + Color.MAIN_COLOR + networkPlayer.getServerName() + Color.SECONDARY_COLOR + "!");
                }
            }
        }

        return false;
    }
}
