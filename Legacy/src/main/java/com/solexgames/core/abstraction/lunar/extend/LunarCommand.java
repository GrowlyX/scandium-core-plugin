package com.solexgames.core.abstraction.lunar.extend;

import com.solexgames.core.abstraction.lunar.AbstractLunarCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LunarCommand extends AbstractLunarCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        return false;
    }
}
