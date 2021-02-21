package vip.potclub.core.command.extend.grant;

import lombok.SneakyThrows;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.menu.extend.grant.GrantMainMenu;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.UUIDUtil;

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
                    Document document = CorePlugin.getInstance().getPlayerManager().getDocumentByUuid(uuid.getKey());

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
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
