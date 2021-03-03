package com.solexgames.core.command.extend.grant;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.menu.extend.grant.GrantMainMenu;
import com.solexgames.core.util.Color;
import lombok.SneakyThrows;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                Document document = CorePlugin.getInstance().getPlayerManager().getDocumentByName(target).orElse(null);

                if (document != null) {
                    new GrantMainMenu(player, document).open(player);
                } else {
                    player.sendMessage(Color.translate("&cThat player does not exist in our databases."));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}
