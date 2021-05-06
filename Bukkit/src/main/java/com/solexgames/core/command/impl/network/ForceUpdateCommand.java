package com.solexgames.core.command.impl.network;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command(label = "forceupdate")
public class ForceUpdateCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        if (!sender.hasPermission("scandium.command.forceupdate")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        RedisUtil.publishAsync(RedisUtil.onServerUpdate());
        sender.sendMessage(Color.SECONDARY_COLOR + "You've force updated this server instance: " + Color.MAIN_COLOR + CorePlugin.getInstance().getServerName());

        return false;
    }
}
