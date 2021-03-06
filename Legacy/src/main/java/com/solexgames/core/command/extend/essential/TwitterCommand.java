package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.server.Network;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TwitterCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Network network = CorePlugin.getInstance().getServerManager().getNetwork();
        sender.sendMessage(network.getSecondaryColor() + "Twitter: " + network.getMainColor() + "@" + network.getTwitterLink());
        return false;
    }
}
