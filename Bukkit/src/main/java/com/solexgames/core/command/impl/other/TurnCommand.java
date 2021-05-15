package com.solexgames.core.command.impl.other;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command(label = "turn")
public class TurnCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.command.crasher")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        if (CorePlugin.getInstance().getPacketHandler() != null) {
            if (args.length == 0) {
                player.sendMessage(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/crasher " + ChatColor.WHITE + "<player>.");
            }
            if (args.length == 1) {
                final Player target = Bukkit.getPlayer(args[0]);

                if (target != null) {
                    CorePlugin.getInstance().getPacketHandler().turnPlayer(target);

                    player.sendMessage(Color.SECONDARY_COLOR + "Turned that player 180 degrees!");
                } else {
                    player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
                }
            }

        } else {
            sender.sendMessage(ChatColor.RED + "Please install ProtocolLib to use this feature.");
        }

        return false;
    }
}
