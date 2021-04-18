package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.external.pagination.impl.NameColorSelectMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ColorCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("scandium.chat.colors")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        new NameColorSelectMenu().openMenu(player);

        return false;
    }
}
