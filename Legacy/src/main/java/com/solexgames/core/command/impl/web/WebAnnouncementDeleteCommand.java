package com.solexgames.core.command.impl.web;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class WebAnnouncementDeleteCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (sender.hasPermission("scandium.command.webannouncementdelete")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <title>."));
        }
        if (args.length > 0) {
            try {
                Document document = new Document("announcementName", StringUtil.buildMessage(args, 0));

                CorePlugin.getInstance().getMongoThread().execute(() ->
                        CorePlugin.getInstance().getCoreDatabase().getWebCollection().deleteOne(document)
                );

                sender.sendMessage(Color.translate("&aDeleted that announcement."));
            } catch (Exception e) {
                sender.sendMessage(Color.translate("&cSomething went wrong while trying to delete the document!"));
            }
        }
        return false;
    }
}
