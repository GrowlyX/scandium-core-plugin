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
        if (!sender.hasPermission("scandium.punishments.kickall")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        Bukkit.getOnlinePlayers().forEach(target -> target.kickPlayer(Color.translate("&cYou were kicked from the server.\n&7Please contact administration if this was a mistake.")));
        sender.sendMessage(Color.translate("&aKicked all online players."));
        return false;
    }
}
