package com.solexgames.core.command.extend.web;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WebAnnouncementDeleteCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        if (player.hasPermission("scandium.command.webannouncementdelete")) {
            if (args.length == 0) {
                sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: /" + serverType.getMainColor() + label + ChatColor.WHITE + " <title>."));
            }

            if (args.length > 0) {
                try {
                    Document document = new Document("announcementName", args[0].replace("_", " "));
                    CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getWebCollection().deleteOne(document));
                    player.sendMessage(Color.translate("&aDeleted that announcement."));
                } catch (Exception e) {
                    player.sendMessage(Color.translate("&cSomething went wrong."));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}
