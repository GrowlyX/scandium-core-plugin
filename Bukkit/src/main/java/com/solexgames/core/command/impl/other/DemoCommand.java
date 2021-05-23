package com.solexgames.core.command.impl.other;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command(label = "demo", permission = "scandium.command.demo")
public class DemoCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (CorePlugin.getInstance().getPacketHandler() != null) {
            if (args.length == 0) {
                final boolean finished = CorePlugin.getInstance().getPacketHandler().sendDemoScreen(player);

                player.sendMessage((finished ? ChatColor.GREEN + "Sent the demo screen to that player." : ChatColor.RED + "Couldn't send the demo screen to that player."));
            }
            if (args.length == 1) {
                final Player target = Bukkit.getPlayer(args[0]);

                if (target != null) {
                    final boolean finished = CorePlugin.getInstance().getPacketHandler().sendDemoScreen(target);

                    player.sendMessage((finished ? ChatColor.GREEN + "Sent the demo screen to that player." : ChatColor.RED + "Couldn't send the demo screen to that player."));
                } else {
                    player.sendMessage(ChatColor.RED + "Error: That player does not exist.");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This feature is currently disabled.");
        }

        return false;
    }
}
