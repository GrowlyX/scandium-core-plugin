package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TwitterCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
        sender.sendMessage(serverType.getSecondaryColor() + "Twitter: " + serverType.getMainColor() + "@" + serverType.getTwitterLink());
        return false;
    }
}
