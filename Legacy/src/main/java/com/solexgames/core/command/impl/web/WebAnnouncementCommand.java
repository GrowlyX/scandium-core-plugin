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
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class WebAnnouncementCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (!sender.hasPermission("scandium.command.webbc")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <title|split with _> <content>."));
        }

        if (args.length > 0) {
            if (args.length == 1) {
                sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <title|split with _> <content>."));
            }
            if (args.length > 1) {
                String title = args[0].replace("_", " ");
                String message = StringUtil.buildMessage(args, 1).replace("<nl>", "\n");

                Document document = new Document();
                document.put("uuid", UUID.randomUUID().toString());

                if (sender instanceof Player) {
                    Player player = (Player) sender;

                    document.put("playerName", player.getName());
                    document.put("playerUuid", player.getUniqueId().toString());
                } else {
                    document.put("playerName", "Console");
                    document.put("playerUuid", "Console");
                }

                Date creation = new Date();

                document.put("announcementName", title);
                document.put("announcementContent", message);
                document.put("time", CorePlugin.FORMAT.format(creation));
                document.put("rawDate", creation.getTime());

                CorePlugin.getInstance().getMongoThread().execute(() ->
                        CorePlugin.getInstance().getCoreDatabase().getWebCollection().insertOne(document)
                );
                sender.sendMessage(Color.translate("&aCreated the web announcement."));
            }
        }
        return false;
    }
}
