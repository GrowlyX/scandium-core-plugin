package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Command(label = "helpop", aliases = "request", hidden = false)
public class HelpOpCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (args.length == 0) {
            player.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <message>");
        }

        if (args.length > 0) {
            final String reason = StringUtil.buildMessage(args, 0);

            if (!potPlayer.isCanRequest()) {
                player.sendMessage(ChatColor.RED + ("You cannot perform this action right now."));
                return false;
            }

            RedisUtil.publishAsync(RedisUtil.onHelpOp(player, reason));
            player.sendMessage(Color.SECONDARY_COLOR + "You've requested help from staff for: " + Color.MAIN_COLOR + reason);

            potPlayer.setCanRequest(false);

            if (CorePlugin.getInstance().getDiscordManager().getClient() != null) {
                CorePlugin.getInstance().getDiscordManager().sendRequest(player, reason);
            }

            Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> {
                final PotPlayer newPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

                if (newPotPlayer != null) {
                    potPlayer.setCanRequest(true);
                }
            }, 60 * 20L);
        }
        return false;
    }
}
