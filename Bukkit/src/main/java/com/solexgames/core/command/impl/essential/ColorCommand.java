package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.external.pagination.impl.NameColorSelectMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ColorCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("scandium.chat.colors")) {
            player.sendMessage(NO_PERMISSION);
            return false;
        }

        new NameColorSelectMenu().openMenu(player);

        return false;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("chatcolor", "chatcolors");
    }
}
