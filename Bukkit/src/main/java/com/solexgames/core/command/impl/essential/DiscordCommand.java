package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class DiscordCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(Color.SECONDARY_COLOR + "Discord: " + Color.MAIN_COLOR + CorePlugin.getInstance().getServerManager().getNetwork().getDiscordLink());
        return false;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("disc");
    }
}
