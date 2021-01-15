package vip.potclub.core.command.extend.essential;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;
import vip.potclub.core.util.StringUtil;

import java.util.stream.Stream;

public class HelpOpCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage(Color.translate("&cUsage: /" + label + " <message>."));
        }

        if (args.length > 0) {
            String reason = StringUtil.buildMessage(args, 0);
            CorePlugin.getInstance().getRedisThread().execute(() -> client.write(RedisUtil.onHelpOp(player, reason)));
            player.sendMessage(Color.translate("&aYour request has been sent to all online staff!"));
        }
        return false;
    }
}
