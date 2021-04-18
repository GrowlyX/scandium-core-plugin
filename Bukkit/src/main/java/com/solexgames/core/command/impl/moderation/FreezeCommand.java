package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (!player.hasPermission("scandium.command.freeze")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player>.");
        }
        if (args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);

            if (target != null) {
                PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

                if (potPlayer.isFrozen()) {
                    potPlayer.setFrozen(false);

                    RedisUtil.writeAsync(RedisUtil.onUnfreeze(player, target));

                    player.sendMessage(ChatColor.GREEN + Color.translate("Unfroze " + target.getDisplayName() + ChatColor.GREEN + "."));
                } else {
                    PotPlayer mainPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
                    PotPlayer targetPotPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);

                    if (mainPotPlayer.getActiveGrant().getRank() != null) {
                        if ((mainPotPlayer.getActiveGrant().getRank().getWeight() >= targetPotPlayer.getActiveGrant().getRank().getWeight()) || player.isOp()) {
                            targetPotPlayer.setFrozen(true);

                            RedisUtil.writeAsync(RedisUtil.onFreeze(player, target));

                            player.sendMessage(ChatColor.GREEN + Color.translate("Froze " + target.getDisplayName() + ChatColor.GREEN + "."));
                        } else {
                            player.sendMessage(ChatColor.RED + ("You cannot freeze this player as their rank weight is higher than yours!"));
                        }
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + ("Error: That player does not exist."));
            }
        }
        return false;
    }
}
