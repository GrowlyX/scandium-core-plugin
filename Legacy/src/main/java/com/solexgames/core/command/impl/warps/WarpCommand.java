package com.solexgames.core.command.impl.warps;

import com.mongodb.client.model.Filters;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.clickable.Clickable;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.warps.Warp;
import com.solexgames.core.util.Color;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand extends BaseCommand {

    public final ServerType NETWORK = CorePlugin.getInstance().getServerManager().getNetwork();

    public void sendHelp(Player player) {
        player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
        player.sendMessage(Color.translate(NETWORK.getMainColor() + ChatColor.BOLD.toString() + "Warp Management:"));
        player.sendMessage("  ");
        player.sendMessage(Color.translate("/warp <warp> &7- Teleport to a warp."));
        player.sendMessage(Color.translate("/warp create &7- Create a new warp."));
        player.sendMessage(Color.translate("/warp delete &7- Delete an existing warp."));
        player.sendMessage(Color.translate("/warp list &7- List all available warps."));
        player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("scandium.command.warp")) {
            player.sendMessage(NO_PERMISSION);
        }

        if (args.length == 0) {
            this.sendHelp(player);
        }
        if (args.length > 0) {
            switch (args[0]) {
                case "list":
                    player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                    player.sendMessage(Color.translate(NETWORK.getMainColor() + ChatColor.BOLD.toString() + "All Warps:"));
                    Warp.getWarps().stream().filter(warp -> warp.getLocation() != null).forEach(warp -> {
                        Clickable chatClickable = new Clickable(ChatColor.GRAY + " * " + ChatColor.YELLOW + warp.getName(), ChatColor.GREEN + "Click to warp to " + ChatColor.RESET + warp.getName() + ChatColor.GREEN + "!", "/warp " + warp.getName());
                        player.spigot().sendMessage(chatClickable.asComponents());
                    });
                    player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                    break;
                case "create":
                    if (args.length == 1) player.sendMessage(ChatColor.RED + ("Usage: /warp create <name>."));
                    if (args.length == 2) {
                        String value = args[1];
                        Warp warp = new Warp(value, player.getLocation(), CorePlugin.getInstance().getServerName());
                        warp.saveWarp();

                        player.sendMessage(ChatColor.GREEN + Color.translate("Created a new warp with the name '" + value + "'."));
                    }
                    break;
                case "delete":
                    if (args.length == 1) player.sendMessage(ChatColor.RED + ("Usage: /warp delete <name>."));
                    if (args.length == 2) {
                        String value = args[1];
                        Warp warp = Warp.getByName(value);

                        if (warp != null) {
                            Warp.getWarps().remove(warp);
                            CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getWarpCollection().deleteOne(Filters.eq("_id", warp.getId())));

                            player.sendMessage(ChatColor.RED + ("Deleted the warp '" + value + "'."));
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That warp does not exist!"));
                        }
                    }
                    break;
                default:
                    String value = args[0];
                    Warp warp = Warp.getByName(value);

                    if (warp != null) {
                        if (warp.getServer().equalsIgnoreCase(CorePlugin.getInstance().getServerName())) {
                            if (warp.getLocation() != null) {
                                player.teleport(warp.getLocation());
                                player.sendMessage(ChatColor.GREEN + Color.translate("Warped you to the &6" + warp.getName() + ChatColor.GREEN + " warp!"));
                            } else {
                                player.sendMessage(ChatColor.RED + ("The location for that warp does not exist!"));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That warp was created on another server!"));
                        }
                    } else {
                        sendHelp(player);
                    }
                    break;
            }
        }
        return false;
    }
}
