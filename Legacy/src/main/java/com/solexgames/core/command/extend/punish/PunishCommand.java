package com.solexgames.core.command.extend.punish;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.extend.punish.PunishMainMenu;
import com.solexgames.core.util.Color;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PunishCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("scandium.command.punish")) {
            ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
            if (args.length == 0) {
                sender.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player>."));
            }
            if (args.length > 0) {
                String target = args[0];
                if (target != null) {
                    if (!target.equals(player.getName())) {
                        new PunishMainMenu(player, target).open(player);
                    } else {
                        player.sendMessage(Color.translate("&cYou cannot punish yourself."));
                    }
                } else {
                    player.sendMessage(Color.translate("&cThat player does not exist."));
                }
            }
        } else {
            player.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
        }
        return false;
    }

}
