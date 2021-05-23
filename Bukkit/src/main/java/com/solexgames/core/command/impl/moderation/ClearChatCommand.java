package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(label = "clearchat", permission = "scandium.command.clearchat", aliases = {"cc"})
public class ClearChatCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        for (int lines = 0; lines < 250; lines++) {
            Bukkit.broadcastMessage(Color.translate("  "));
        }

        final boolean silent = args[0] != null && args[0].equals("-s");
        final boolean randomized = args[1] != null && args[1].equals("-r");
        final int amount = randomized ? CorePlugin.RANDOM.nextInt(250) : 250;
        final String broadcast = silent ? ChatColor.RED + "The chat has been cleared by " + ChatColor.BOLD + "Staff" + ChatColor.RED + "." : "The chat has been cleared by " + (sender instanceof Player ? ((Player) sender).getDisplayName() : ChatColor.DARK_RED + "Console") + ChatColor.GREEN + ".";

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> !player.hasPermission("scandium.staff"))
                .forEach(player -> {
                    for (int lines = 0; lines <= amount; lines++) {
                        player.sendMessage("  ");
                    }
                });

        Bukkit.broadcastMessage(broadcast);

        if (sender instanceof Player) {
            PlayerUtil.sendAlert((Player) sender, "cleared chat");
        }

        return false;
    }
}
