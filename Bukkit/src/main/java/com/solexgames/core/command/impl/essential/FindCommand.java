package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.global.NetworkPlayer;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FindCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("scandium.command.find")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player>.");
        }

        if (args.length > 0) {
            final String target = args[0];
            final NetworkPlayer networkPlayer = CorePlugin.getInstance().getPlayerManager().getNetworkPlayer(target);

            sender.sendMessage(Color.SECONDARY_COLOR + "Searching for that player...");

            if (networkPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Error: The player with the specified name does not exist.");
            } else {
                Rank rank = Rank.getByName(networkPlayer.getRankName());
                String displayName = Color.translate((rank != null ? rank.getColor() + rank.getItalic() : ChatColor.GRAY) + networkPlayer.getName());

                sender.sendMessage(Color.MAIN_COLOR + displayName + Color.SECONDARY_COLOR + " is currently online " + Color.MAIN_COLOR + networkPlayer.getServerName() + Color.SECONDARY_COLOR + "!");
            }
        }

        return false;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("search", "lookup");
    }

    @Override
    public boolean isHidden() {
        return false;
    }
}
