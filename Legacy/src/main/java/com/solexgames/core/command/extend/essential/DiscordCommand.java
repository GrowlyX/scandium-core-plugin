package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DiscordCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        sender.sendMessage(serverType.getSecondaryColor() + "Discord: " + serverType.getMainColor() + serverType.getDiscordLink());
        return false;
    }
}
