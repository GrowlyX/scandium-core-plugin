package com.solexgames.core.command.impl.grant;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.UUIDUtil;
import com.solexgames.core.util.external.pagination.impl.GrantMainPaginatedMenu;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GrantCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.grant")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player>.");
        }
        if (args.length > 0) {
            UUID uuid = UUIDUtil.fetchUUID(args[0]);

            if (uuid == null) {
                player.sendMessage(ChatColor.RED + "Error: That player is not valid.");
                return false;
            }

            Document document = CorePlugin.getInstance().getPlayerManager().getDocumentByUuid(uuid).orElse(null);

            if (document != null) {
                new GrantMainPaginatedMenu(document, player).openMenu(player);
            } else {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist in our database.");
            }
        }

        return false;
    }
}
