package com.solexgames.core.command.extend.warps;

import com.mongodb.client.model.Filters;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.clickable.Clickable;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.server.Network;
import com.solexgames.core.player.warps.Warp;
import com.solexgames.core.util.Color;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand extends BaseCommand {

    public final Network NETWORK = CorePlugin.getInstance().getServerManager().getNetwork();

    public void sendHelp(Player player) {
        player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
        player.sendMessage(Color.translate(NETWORK.getMainColor() + ChatColor.BOLD.toString() + "Warp Management:"));
        player.sendMessage(Color.translate("/warp <warp> &7- Teleport to a warp."));
        player.sendMessage(Color.translate("/warp create &7- Create a new warp."));
        player.sendMessage(Color.translate("/warp delete &7- Delete an existing warp."));
        player.sendMessage(Color.translate("/warp list &7- List all available warps."));
        player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.warp")) {
            if (args.length == 0) sendHelp(player);
            if (args.length > 0) {
                if (player.hasPermission("scandium.warp.management")) {
                    switch (args[0]) {
                        case "list":
                            player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                            player.sendMessage(Color.translate(NETWORK.getMainColor() + ChatColor.BOLD.toString() + "All Warps:"));
                            Warp.getWarps().forEach(warp -> {
                                Clickable chatClickable = new Clickable(ChatColor.GRAY + " * " + ChatColor.YELLOW + warp.getName(), ChatColor.GREEN + "Click to warp to " + ChatColor.RESET + warp.getName() + ChatColor.GREEN + "!", "/warp " + warp.getName());
                                player.spigot().sendMessage(chatClickable.asComponents());
                            });
                            player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                            break;
                        case "create":
                            if (args.length == 1) player.sendMessage(Color.translate("&cUsage: /warp create <name>."));
                            if (args.length == 2) {
                                String value = args[1];
                                Warp warp = new Warp(value, player.getLocation());
                                warp.saveWarp();

                                player.sendMessage(Color.translate("&aCreated a new warp with the name '" + value + "'."));
                            }
                            break;
                        case "delete":
                            if (args.length == 1) player.sendMessage(Color.translate("&cUsage: /warp delete <name>."));
                            if (args.length == 2) {
                                String value = args[1];
                                Warp warp = Warp.getByName(value);

                                if (warp != null) {
                                    Warp.getWarps().remove(warp);
                                    CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getWarpCollection().deleteOne(Filters.eq("_id", warp.getId())));

                                    player.sendMessage(Color.translate("&cDeleted the warp '" + value + "'."));
                                } else {
                                    player.sendMessage(Color.translate("&cThat warp does not exist!"));
                                }
                            }
                            break;
                        default:
                            String value = args[0];
                            Warp warp = Warp.getByName(value);

                            if (warp != null) {
                                player.teleport(warp.getLocation());
                                player.sendMessage(Color.translate("&aWarped you to the &6" + warp.getName() + "&a warp!"));
                            } else sendHelp(player);
                            break;
                    }
                } else {
                    player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}
