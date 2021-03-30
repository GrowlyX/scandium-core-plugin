package com.solexgames.core.command.impl.test;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.external.pagination.extend.PaginationTestingMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        new PaginationTestingMenu().openMenu(player);

        return false;
    }
}
