package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpOpCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        if (args.length == 0) {
            player.sendMessage(Color.translate("&cUsage: /" + label + " <message>."));
        }

        if (args.length > 0) {
            String reason = StringUtil.buildMessage(args, 0);
            if (potPlayer.isCanRequest()) {
                CorePlugin.getInstance().getRedisThread().execute(() -> client.write(RedisUtil.onHelpOp(player, reason)));
                player.sendMessage(Color.translate("&aYour request has been sent to all online staff!"));

                potPlayer.setCanRequest(false);
                Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> {
                    PotPlayer newPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
                    if (newPotPlayer != null) {
                        potPlayer.setCanRequest(true);
                    }
                }, 60 * 20L);
            } else {
                player.sendMessage(Color.translate("&cYou cannot do that right now."));
            }
        }
        return false;
    }
}
