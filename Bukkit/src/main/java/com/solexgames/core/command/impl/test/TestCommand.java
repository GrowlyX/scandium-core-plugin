package com.solexgames.core.command.impl.test;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import org.bukkit.command.CommandSender;

@Command(label = "test", aliases = {"test2"}, hidden = false /* if it shows in tab completion for non staff */, async = true)
public class TestCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        return false;
    }
}
