package vip.potclub.core.lunar.extend;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import vip.potclub.core.lunar.AbstractClientInjector;

public class LunarCommand extends AbstractClientInjector {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }
}
