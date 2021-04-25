package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IgnoreCommand extends BaseCommand {

    public void sendHelp(Player player) {
        player.sendMessage(this.getHelpMessage(
                "/ignore <player>",
                "/ignore list",
                "  ",
                "/unignore <player>"
        ));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (args.length == 0) {
            this.sendHelp(player);
        }

        if (args.length > 0) {
            final String value = args[0];

            switch (label.toLowerCase()) {
                case "unignore":
                    if (!potPlayer.getName().equalsIgnoreCase(value)) {
                        player.sendMessage(ChatColor.RED + ("You cannot remove yourself from your ignore list!"));
                        return false;
                    }

                    if (!potPlayer.getAllIgnoring().contains(value)) {
                        player.sendMessage(ChatColor.RED + ("That player's not on your ignore list!"));
                        return false;
                    }

                    potPlayer.getAllIgnoring().remove(value);
                    player.sendMessage(Color.SECONDARY_COLOR + "You've removed " + Color.MAIN_COLOR + value + Color.SECONDARY_COLOR + " from your ignored list!");

                    break;
                case "ignore":
                    switch (value.toLowerCase()) {
                        case "list":
                            if (potPlayer.getAllIgnoring().isEmpty()) {
                                player.sendMessage(ChatColor.RED + ("Error: You do not have anyone added to your ignore list."));
                                return false;
                            }

                            player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                            player.sendMessage(Color.translate(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "Currently Ignoring:"));
                            potPlayer.getAllIgnoring().forEach(s -> player.sendMessage(Color.translate(" &7* &e" + s)));
                            player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));

                            break;
                        default:
                            if (potPlayer.getName().equalsIgnoreCase(value)) {
                                player.sendMessage(ChatColor.RED + ("You cannot add yourself to your ignore list!"));
                                return false;
                            }

                            if (potPlayer.getAllIgnoring().contains(value)) {
                                player.sendMessage(ChatColor.RED + ("That player's already on your ignore list!"));
                                return false;
                            }

                            potPlayer.getAllIgnoring().add(value);
                            player.sendMessage(Color.SECONDARY_COLOR + "You've added " + Color.MAIN_COLOR + value + Color.SECONDARY_COLOR + " to your ignored list.");

                            break;
                    }
                    break;
            }
        }
        return false;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("unignore");
    }
}
