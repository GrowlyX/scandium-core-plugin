package vip.potclub.core.command.extend;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import vip.potclub.core.command.BaseCommand;

public class CoreCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
}
