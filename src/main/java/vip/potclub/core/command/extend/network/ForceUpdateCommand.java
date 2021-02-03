package vip.potclub.core.command.extend.network;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;

public class ForceUpdateCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof ConsoleCommandSender) {
            System.out.println("no");
            return false;
        }

        Player player = (Player) commandSender;

        if (player.hasPermission("scandium.command.forceupdate")) {
            if (args.length == 0) {
                CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onServerUpdate()));
                player.sendMessage(Color.translate("&aForce-updated the server."));
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
