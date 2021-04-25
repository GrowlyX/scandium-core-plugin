package com.solexgames.core.command.impl.essential;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class StoreCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(Color.SECONDARY_COLOR + "Store: " + Color.MAIN_COLOR + CorePlugin.getInstance().getServerManager().getNetwork().getStoreLink());
        return false;
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }
}
