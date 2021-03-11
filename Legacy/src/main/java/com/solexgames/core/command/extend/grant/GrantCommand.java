package com.solexgames.core.command.extend.grant;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.UUIDUtil;
import com.solexgames.core.util.external.pagination.extend.GrantMainPaginatedMenu;
import lombok.SneakyThrows;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class GrantCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.grant")) {
            ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
            if (args.length == 0) {
                sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player>."));
            }
            if (args.length > 0) {
                String target = args[0];
                Document document = CorePlugin.getInstance().getPlayerManager().getDocumentByUuid(Objects.requireNonNull(UUIDUtil.getId(target))).orElse(null);

                if (document != null) {
                    new GrantMainPaginatedMenu(document, player).openMenu(player);
                } else {
                    player.sendMessage(Color.translate("&cThat player does not exist in our databases."));
                }
            }
        } else {
            player.sendMessage(NO_PERMISSION);
        }
        return false;
    }
}
