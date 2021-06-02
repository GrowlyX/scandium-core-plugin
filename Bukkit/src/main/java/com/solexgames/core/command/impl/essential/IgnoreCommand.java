package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.builder.PageListBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Command(label = "ignore", aliases = "unignore", hidden = false)
public class IgnoreCommand extends BaseCommand {

    public void sendHelp(Player player) {
        this.getHelpMessage(1, player,
                "/ignore <player>",
                "/ignore list",
                "/unignore <player>"
        );
    }

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
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
                    if (potPlayer.getName().equalsIgnoreCase(value)) {
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
                    if ("list".equals(value.toLowerCase())) {
                        if (potPlayer.getAllIgnoring().isEmpty()) {
                            player.sendMessage(ChatColor.RED + ("Error: You do not have anyone added to your ignore list."));
                            return false;
                        }

                        final PageListBuilder listBuilder = new PageListBuilder(50, "Ignored players");

                        listBuilder.display(sender, 1, potPlayer.getAllIgnoring());
                    } else {
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
                    }
                    break;
            }
        }
        return false;
    }
}
