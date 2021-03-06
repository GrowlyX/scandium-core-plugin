package com.solexgames.core.command.extend.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class WebsiteCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        sender.sendMessage(network.getSecondaryColor() + "Website: " + network.getMainColor() + network.getWebsiteLink());
        return false;
    }
}
