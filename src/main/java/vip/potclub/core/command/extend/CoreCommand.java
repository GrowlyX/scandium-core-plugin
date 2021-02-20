package vip.potclub.core.command.extend;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.command.BaseCommand;
import vip.potclub.core.menu.extend.ScandiumMenu;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.StringUtil;

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
                player.sendMessage(Color.translate("&cUsage: /" + label + " <debug|disallow|panel>"));
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
                    case "panel":
                        new ScandiumMenu(player).open(player);
                        break;
                }
            }
        } else {
            StringUtil.sendCenteredMessage(player, "&a&m" + StringUtils.repeat("-", 53));
            StringUtil.sendCenteredMessage(player, "&eThis server is running &6Scandium Core&e.");
            StringUtil.sendCenteredMessage(player, "&eDeveloped by &bGrowlyX#1337&e.");
            StringUtil.sendCenteredMessage(player, "&a&m" + StringUtils.repeat("-", 53));
        }
        return false;
    }
}
