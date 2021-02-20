package vip.potclub.core.command.extend.test;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.kotlin.test.Moose;

public class KotlinCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Moose moose = new Moose("Ben");
        Moose.UTILITY.sendPlayerMooseName(moose, (Player) commandSender);
        return false;
    }
}
