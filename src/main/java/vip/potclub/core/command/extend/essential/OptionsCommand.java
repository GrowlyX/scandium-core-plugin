package vip.potclub.core.command.extend.essential;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.enums.ChatChannelType;
import vip.potclub.core.menu.extend.SettingsMenu;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.RedisUtil;
import vip.potclub.core.util.StringUtil;

public class OptionsCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            new SettingsMenu(player).open(player);
        }
        return false;
    }
}
