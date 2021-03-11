package com.solexgames.core.command.extend.punish;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickAllCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.punishments.kickall")) {
            if (args.length == 0) {
                Bukkit.getOnlinePlayers().forEach(target -> {
                    if (target != player) {
                        target.kickPlayer(Color.translate("&cYou were kicked from the server.\n&cPlease contact administration if this was a mistake."));
                    }
                });
                player.sendMessage(Color.translate("&aKicked all online players."));
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }
}
