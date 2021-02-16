package vip.potclub.core.command.extend.essential;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;

public class MuteChatCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.mutechat")) {
            if (args.length == 0) {
                if (CorePlugin.getInstance().getServerManager().isChatEnabled()) {
                    CorePlugin.getInstance().getServerManager().setChatEnabled(false);
                    Bukkit.broadcastMessage(Color.translate("&aThe chat has been disabled by " + player.getDisplayName() + "&a."));
                    RedisUtil.writeAsync(RedisUtil.onGlobalBroadcastPermission(Color.translate("&3[S] " + "&7[" + CorePlugin.getInstance().getServerName() + "] " + player.getDisplayName() + " &bhas disabled global chat&b."), "scandium.staff"));
                } else if (!CorePlugin.getInstance().getServerManager().isChatEnabled()) {
                    CorePlugin.getInstance().getServerManager().setChatEnabled(true);
                    Bukkit.broadcastMessage(Color.translate("&aThe chat has been enabled by " + player.getDisplayName() + "&a."));
                    RedisUtil.writeAsync(RedisUtil.onGlobalBroadcastPermission(Color.translate("&3[S] " + "&7[" + CorePlugin.getInstance().getServerName() + "] " + player.getDisplayName() + " &bhas enabled global chat&b."), "scandium.staff"));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cNo permission."));
        }
        return false;
    }
}
