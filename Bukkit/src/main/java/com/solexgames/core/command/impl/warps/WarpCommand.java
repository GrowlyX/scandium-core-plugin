package com.solexgames.core.command.impl.warps;

import com.mongodb.client.model.Filters;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.clickable.Clickable;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.warps.Warp;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.external.pagination.impl.NameColorSelectMenu;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WarpCommand extends BaseCommand {

    public void sendHelp(Player player) {
        this.getHelpMessage(1, player,
                "/warp <warp>",
                "/warp create <name>",
                "/warp delete <name>",
                "/warp list"
        );
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.warp")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            this.sendHelp(player);
        }
        if (args.length > 0) {
            switch (args[0]) {
                case "list":
                    player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                    player.sendMessage(Color.translate(Color.MAIN_COLOR + ChatColor.BOLD.toString() + "All Warps:"));
                    Warp.getWarps().stream().filter(warp -> warp.getLocation() != null).forEach(warp -> {
                        final Clickable chatClickable = new Clickable(ChatColor.GRAY + " * " + ChatColor.YELLOW + warp.getName(), ChatColor.GREEN + "Click to warp to " + ChatColor.RESET + warp.getName() + ChatColor.GREEN + "!", "/warp " + warp.getName());
                        player.spigot().sendMessage(chatClickable.asComponents());
                    });
                    player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 53)));
                    break;
                case "create":
                    if (args.length == 1) player.sendMessage(ChatColor.RED + ("Usage: /warp create <name>."));
                    if (args.length == 2) {
                        final String value = args[1];
                        final Warp warp = new Warp(value, player.getLocation(), CorePlugin.getInstance().getServerName());
                        warp.saveWarp();

                        player.sendMessage(ChatColor.GREEN + Color.translate("Created a new warp with the name '" + value + "'."));
                    }
                    break;
                case "delete":
                    if (args.length == 1) player.sendMessage(ChatColor.RED + ("Usage: /warp delete <name>."));
                    if (args.length == 2) {
                        final String value = args[1];
                        final Warp warp = Warp.getByName(value);

                        if (warp != null) {
                            Warp.getWarps().remove(warp);
                            CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getWarpCollection().deleteOne(Filters.eq("_id", warp.getId())));

                            player.sendMessage(ChatColor.RED + ("Deleted the warp '" + value + "'."));
                        } else {
                            player.sendMessage(ChatColor.RED + ("Error: That warp does not exist!"));
                        }
                    }
                    break;
                default:
                    final String value = args[0];
                    final Warp warp = Warp.getByName(value);

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
                        this.sendHelp(player);
                    }
                    break;
            }
        }
        return false;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("warps");
    }
}
