package vip.potclub.core.command.extend.essential;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.util.Color;

public class ProfileCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        player.sendMessage(Color.translate(CorePlugin.getInstance().getServerManager().getNetwork().getSecondaryColor() + "Your Profile: " + CorePlugin.getInstance().getServerManager().getNetwork().getMainColor() + "https://" + CorePlugin.getInstance().getServerManager().getNetwork().getWebsiteLink() + "/u/" + player.getName() + "/"));
        return false;
    }
}
