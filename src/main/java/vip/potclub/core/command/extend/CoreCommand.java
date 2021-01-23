package vip.potclub.core.command.extend;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.util.Color;

public class CoreCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.getUniqueId().equals(CorePlugin.getInstance().getServerManager().getNetwork().getMainOwner()) || player.getUniqueId().equals(CorePlugin.getInstance().getServerManager().getNetwork().getMainDeveloper())) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <debug|disallow|destroy>"));
            }
            if (args.length > 0) {
                switch (args[0]) {
                    case "debug":
                        player.sendMessage(Color.translate((CorePlugin.getInstance().isDebugging() ? "&b[C] &cDisabled debugging." : "&b[C] &aEnabled debugging.")));
                        CorePlugin.getInstance().setDebugging(!CorePlugin.getInstance().isDebugging());
                        break;
                    case "disallow":
                        player.sendMessage(Color.translate((CorePlugin.getInstance().isDisallow() ? "&b[C] &cDisabled disallow." : "&b[C] &aEnabled disallow.")));
                        CorePlugin.getInstance().setDisallow(!CorePlugin.getInstance().isDisallow());
                        break;
                    case "destroy":
                        player.sendMessage(Color.translate("&cBoom! Destroyed!"));
                        Bukkit.getServer().shutdown();
                        break;
                }
            }
        } else {
            player.sendMessage(Color.translate("&cThis command is restricted."));
        }
        return false;
    }
}
