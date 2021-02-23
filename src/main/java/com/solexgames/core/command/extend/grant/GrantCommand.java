package com.solexgames.core.command.extend.grant;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.menu.extend.grant.GrantMainMenu;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.UUIDUtil;
import lombok.SneakyThrows;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map.Entry;
import java.util.UUID;

public class GrantCommand extends BaseCommand {

    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.grant")) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <player>."));
            }
            if (args.length > 0) {
                String target = args[0];
                Entry<UUID, String> uuid = UUIDUtil.getUUID(target);

                if ((uuid.getKey() != null)) {
                    Document document = CorePlugin.getInstance().getPlayerManager().getDocumentByUuid(uuid.getValue());

                    if (document != null) {
                        new GrantMainMenu(player, document).open(player);
                    } else {
                        player.sendMessage(Color.translate("&cThat player does not exist in our databases."));
                    }
                } else {
                    player.sendMessage(Color.translate("&cThat minecraft profile does not exist."));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}
