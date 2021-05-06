package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(label = "twitter", hidden = false)
public class TwitterCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        sender.sendMessage(Color.SECONDARY_COLOR + "Twitter: " + Color.MAIN_COLOR + CorePlugin.getInstance().getServerManager().getNetwork().getTwitterLink());
        return false;
    }
}
