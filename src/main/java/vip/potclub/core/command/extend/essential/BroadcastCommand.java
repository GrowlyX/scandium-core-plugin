package vip.potclub.core.command.extend.essential;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;
import vip.potclub.core.util.StringUtil;

public class BroadcastCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("core.command.broadcast")) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <message>."));
            }

            if (args.length > 0) {
                String message = StringUtil.buildMessage(args, 0);
                CorePlugin.getInstance().getRedisThread().execute(() -> client.write(RedisUtil.onBroadcast(message)));
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
