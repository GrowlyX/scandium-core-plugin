package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(label = "heal", permission = "scandium.command.heal")
public class HealCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            player.setHealth(20);
            player.sendMessage(Color.SECONDARY_COLOR + "Set your health level to " + Color.MAIN_COLOR + "20" + Color.SECONDARY_COLOR +".");

            PlayerUtil.sendAlert(player, "healed");
        }
        if (args.length > 0) {
            final Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                target.setHealth(20);
                player.sendMessage(Color.SECONDARY_COLOR + "Set " + target.getDisplayName() + " health level to " + Color.MAIN_COLOR + "20" + Color.SECONDARY_COLOR +".");

                PlayerUtil.sendAlert(player, "healed " + target.getName());
            } else {
                player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
            }
        }
        return false;
    }
}
