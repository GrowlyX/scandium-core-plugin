package com.solexgames.core.command.extend.grant;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.UUIDUtil;
import com.solexgames.core.util.external.pagination.extend.GrantMainPaginatedMenu;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class GrantCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (!player.hasPermission("scandium.command.grant")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player>.");
        }
        if (args.length > 0) {
            UUID uuid = UUIDUtil.fetchUUID(args[0]);

            if (uuid == null) {
                player.sendMessage(ChatColor.RED + "That uuid is not valid.");
                return false;
            }

            AtomicReference<Document> document = new AtomicReference<>();
            CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

            CompletableFuture.runAsync(() -> {
                document.set(CorePlugin.getInstance().getPlayerManager().getDocumentByUuid(uuid).orElse(null));
                completableFuture.complete(true);
            });

            completableFuture.thenRun(() -> {
                if (document.get() != null) {
                    new GrantMainPaginatedMenu(document.get(), player).openMenu(player);
                } else {
                    player.sendMessage(ChatColor.RED + "That player does not exist in our databasesw.");
                }
            });
        }
        return false;
    }
}
