package com.solexgames.core.command.impl.moderation;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.external.pagination.impl.ReportViewPaginatedMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportsCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
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
