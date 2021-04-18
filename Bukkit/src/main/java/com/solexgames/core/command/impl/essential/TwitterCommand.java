package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TwitterCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(Color.SECONDARY_COLOR + "Twitter: " + Color.MAIN_COLOR + CorePlugin.getInstance().getServerManager().getNetwork().getTwitterLink());
        return false;
    }
}
