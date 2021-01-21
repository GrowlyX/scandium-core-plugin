package vip.potclub.core.command.extend.essential;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;
import vip.potclub.core.util.StringUtil;

public class ReportCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        PotPlayer potPlayer = PotPlayer.getPlayer(player);

        if (args.length == 0) {
            player.sendMessage(Color.translate("&cUsage: /" + label + " <player> <message>."));
        }

        if (args.length > 0) {
            if (args.length == 1) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <player> <message>."));
            }
            if (args.length == 2) {
                Player target = Bukkit.getPlayerExact(args[0]);
                String message = StringUtil.buildMessage(args, 1);

                if (target != null) {
                    if (potPlayer.isCanReport()) {
                        CorePlugin.getInstance().getRedisThread().execute(() -> client.write(RedisUtil.onReport(player, target, message)));
                        player.sendMessage(Color.translate("&aYour request has been sent to all online staff!"));

                        potPlayer.setCanReport(false);
                        Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> {
                            // redundant, but checking if player is online or not.
                            if (potPlayer != null) {
                                potPlayer.setCanReport(true);
                            }
                        }, 60 * 20L);
                    } else {
                        player.sendMessage(Color.translate("&cYou cannot do that right now."));
                    }
                } else {
                    player.sendMessage(Color.translate("&cThat player does not exist."));
                }
            }
        }
        return false;
    }
}
