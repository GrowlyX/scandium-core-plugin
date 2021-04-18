package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DiscordCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(Color.SECONDARY_COLOR + "Discord: " + Color.MAIN_COLOR + CorePlugin.getInstance().getServerManager().getNetwork().getDiscordLink());
        return false;
    }
}
