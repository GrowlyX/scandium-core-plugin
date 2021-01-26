package vip.potclub.core.command.extend.essential.web;

import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.util.Color;

public class WebAnnouncementDeleteCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.webannouncementdelete")) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <title>."));
            }

            if (args.length > 0) {
                try {
                    Document document = new Document("announcementName", args[0].replace("_", " "));
                    CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getWebCollection().deleteOne(document));
                } catch (Exception e) {
                    player.sendMessage(Color.translate("&cSomething went wrong."));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
