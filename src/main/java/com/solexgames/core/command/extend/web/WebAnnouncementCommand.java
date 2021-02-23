package com.solexgames.core.command.extend.web;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class WebAnnouncementCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                sender.sendMessage(Color.translate("&cUsage: /" + label + " <title|split with _> <content>."));
            }

            if (args.length > 0) {
                if (args.length == 1) {
                    sender.sendMessage(Color.translate("&cUsage: /" + label + " <title|split with _> <content>."));
                }
                if (args.length == 2) {
                    String title = args[0].replace("_", " ");
                    String message = StringUtil.buildMessage(args, 1).replace("<nl>", "\n");

                    Document document = new Document();
                    document.put("uuid", UUID.randomUUID());
                    document.put("playerName", "Console");
                    document.put("playerUuid", "Console");
                    document.put("announcementName", title);
                    document.put("announcementContent", message);
                    document.put("time", CorePlugin.FORMAT.format(new Date()));
                    document.put("rawDate", new Date().getTime());

                    CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getWebCollection().insertOne(document));
                }
            }
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.webbc")) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <title|split with _> <content>."));
            }

            if (args.length > 0) {
                if (args.length == 1) {
                    player.sendMessage(Color.translate("&cUsage: /" + label + " <title|split with _> <content>."));
                }
                if (args.length > 1) {
                    String title = args[0].replace("_", " ");
                    String message = StringUtil.buildMessage(args, 1).replace("<nl>", "\n");

                    Document document = new Document();
                    document.put("uuid", UUID.randomUUID().toString());
                    document.put("playerName", player.getName());
                    document.put("playerUuid", player.getUniqueId().toString());
                    document.put("announcementName", title);
                    document.put("announcementContent", message);
                    document.put("time", CorePlugin.FORMAT.format(new Date()));
                    document.put("rawDate", new Date().getTime());

                    CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getWebCollection().insertOne(document));
                    player.sendMessage(Color.translate("&aCreated the web announcement."));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
