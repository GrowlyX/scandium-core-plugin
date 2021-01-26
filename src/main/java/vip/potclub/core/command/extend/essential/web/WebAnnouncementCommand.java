package vip.potclub.core.command.extend.essential.web;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.enums.ChatChannelType;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.StringUtil;

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
                if (args.length == 2) {
                    String title = args[0].replace("_", " ");
                    String message = StringUtil.buildMessage(args, 1).replace("<nl>", "\n");

                    Document document = new Document();
                    document.put("uuid", UUID.randomUUID());
                    document.put("playerName", player.getName());
                    document.put("playerUuid", player.getUniqueId().toString());
                    document.put("announcementName", title);
                    document.put("announcementContent", message);
                    document.put("time", CorePlugin.FORMAT.format(new Date()));
                    document.put("rawDate", new Date().getTime());

                    CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getWebCollection().insertOne(document));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
