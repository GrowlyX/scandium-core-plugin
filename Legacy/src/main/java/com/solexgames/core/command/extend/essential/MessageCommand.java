package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage(Color.translate("&cUsage: /" + label + " <player> <message>."));
        }
        if (args.length > 0) {
            if (args.length == 1) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <player> <message>."));
            }
            if (args.length > 1) {
                Player target = Bukkit.getPlayerExact(args[0]);
                String message = StringUtil.buildMessage(args, 1);

                if (target != null) {
                    PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(target);
                    PotPlayer potPerson = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

                    if (potPlayer.isIgnoring(potPerson.getPlayer())) {
                        if (potPerson.isIgnoring(potPlayer.getPlayer())) {
                            if (potPerson.isCanReceiveDms()) {
                                if (potPerson != potPlayer) {
                                    if (potPlayer.isCanReceiveDms()) {
                                        StringUtil.sendPrivateMessage(player, target, message);

                                        potPerson.setLastRecipient(target);
                                        potPlayer.setLastRecipient(player);
                                    } else {
                                        player.sendMessage(Color.translate("&cThat player has their dms disabled."));
                                    }
                                } else {
                                    player.sendMessage(Color.translate("&cYou cannot message yourself."));
                                }
                            } else {
                                player.sendMessage(Color.translate("&cYou have your dms disabled."));
                            }
                        } else {
                            player.sendMessage(Color.translate("&cYou are currently ignoring that player."));
                        }
                    } else {
                        player.sendMessage(Color.translate("&cThat player is currently ignoring you."));
                    }
                } else {
                    player.sendMessage(Color.translate("&cThat player does not exist."));
                }
            }
        }
        return false;
    }
}
