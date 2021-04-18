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

public class IgnoreCommand extends BaseCommand {

    public final ServerType NETWORK = CorePlugin.getInstance().getServerManager().getNetwork();

    public void sendHelp(Player player) {
        player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
        player.sendMessage(Color.translate(NETWORK.getMainColor() + ChatColor.BOLD.toString() + "Ignore Commands:"));
        player.sendMessage(Color.translate("/ignore <player> &7- Add a player to your ignore list."));
        player.sendMessage(Color.translate("/ignore list &7- View your ignore list."));
        player.sendMessage(Color.translate("/unignore <player> &7- Remove a player from your ignore list."));
        player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0)
            this.sendHelp(player);

        if (args.length > 0) {
            if (label.equalsIgnoreCase("unignore")) {
                String value = args[0];
                PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

                if (!potPlayer.getName().equalsIgnoreCase(value)) {
                    if (potPlayer.getAllIgnoring().contains(value)) {
                        potPlayer.getAllIgnoring().remove(value);
                        player.sendMessage(ChatColor.GREEN + Color.translate("Removed " + value + " from your ignore list."));
                    } else {
                        player.sendMessage(ChatColor.RED + ("You don't have that player on your ignore list."));
                    }
                } else {
                    player.sendMessage(ChatColor.RED + ("You cannot remove yourself to your ignore list!"));
                }
            } else if (label.equalsIgnoreCase("ignore")) {
                PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
                if ("list".equals(args[0])) {
                    if (potPlayer.getAllIgnoring().isEmpty()) {
                        player.sendMessage(ChatColor.RED + ("Error: You do not have anyone added to your ignore list."));
                        return false;
                    }

                    player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                    player.sendMessage(Color.translate(NETWORK.getMainColor() + ChatColor.BOLD.toString() + "Currently Ignoring:"));
                    potPlayer.getAllIgnoring().forEach(s -> player.sendMessage(Color.translate(" &7* &e" + s)));
                    player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                } else {
                    String value = args[0];

                    if (!potPlayer.getName().equalsIgnoreCase(value)) {
                        if (!potPlayer.getAllIgnoring().contains(value)) {
                            potPlayer.getAllIgnoring().add(value);
                            player.sendMessage(ChatColor.GREEN + Color.translate("Added " + value + " to your ignore list."));
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That player is already on your ignore list."));
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + ("You cannot add yourself to your ignore list!"));
                    }
                }
            }
        }
        return false;
    }
}
