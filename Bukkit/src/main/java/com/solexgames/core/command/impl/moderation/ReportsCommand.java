package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.external.impl.ReportViewPaginatedMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Command(label = "reports")
public class ReportsCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.commands.reports")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        new ReportViewPaginatedMenu().openMenu(player);

        return false;
    }
}
