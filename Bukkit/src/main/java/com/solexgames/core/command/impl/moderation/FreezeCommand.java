package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Command(label = "freeze", aliases = "ss")
public class FreezeCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.freeze")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player>.");
        }
        if (args.length == 1) {
            final Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

                if (potPlayer.isFrozen()) {
                    potPlayer.setFrozen(false);

                    RedisUtil.publishAsync(RedisUtil.onUnfreeze(player, target));

                    player.sendMessage(ChatColor.GREEN + Color.translate("Unfroze " + target.getDisplayName() + ChatColor.GREEN + "."));
                } else {
                    final PotPlayer mainPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
                    final  PotPlayer targetPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

                    if (mainPotPlayer.getActiveGrant().getRank() != null) {
                        if ((mainPotPlayer.getActiveGrant().getRank().getWeight() >= targetPotPlayer.getActiveGrant().getRank().getWeight()) || player.isOp()) {
                            targetPotPlayer.setFrozen(true);

                            RedisUtil.publishAsync(RedisUtil.onFreeze(player, target));

                            player.sendMessage(ChatColor.GREEN + Color.translate("Froze " + target.getDisplayName() + ChatColor.GREEN + "."));
                        } else {
                            player.sendMessage(ChatColor.RED + ("You cannot freeze this player as their rank weight is higher than yours!"));
                        }
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
            }
        }
        return false;
    }
}
