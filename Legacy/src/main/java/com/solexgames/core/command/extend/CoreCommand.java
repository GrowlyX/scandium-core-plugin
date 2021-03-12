package com.solexgames.core.command.extend;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.extend.ScandiumMenu;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CoreCommand extends BukkitCommand {

    public CoreCommand(String name) {
        super(name, "Base Command for " + name + " Core.", "/" + name + " <debug|disallow|panel>", Arrays.asList("core", "suite"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("");
            return false;
        }

        Player player = (Player) sender;
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();
        if (player.isOp()) {
            if (args.length == 0) {
                sender.sendMessage(Color.translate(network.getSecondaryColor() + "Usage: /" + network.getMainColor() + label + ChatColor.WHITE + " <debug|disallow|panel>."));
            }
            if (args.length > 0) {
                switch (args[0]) {
                    case "debug":
                        player.sendMessage(Color.translate((CorePlugin.getInstance().isDebugging() ? network.getMainColor() + "[" + CorePlugin.getInstance().getConfig().getString("core-settings.name") + "] &cDisabled debugging." : network.getMainColor() + "[" + CorePlugin.getInstance().getConfig().getString("core-settings.name") + "] &aEnabled debugging.")));
                        CorePlugin.getInstance().setDebugging(!CorePlugin.getInstance().isDebugging());
                        break;
                    case "disallow":
                        player.sendMessage(Color.translate((CorePlugin.getInstance().isDisallow() ? network.getMainColor() + "[" + CorePlugin.getInstance().getConfig().getString("core-settings.name") + "] &cDisabled disallow." : network.getMainColor() + "[" + CorePlugin.getInstance().getConfig().getString("core-settings.name") + "] &aEnabled disallow.")));
                        CorePlugin.getInstance().setDisallow(!CorePlugin.getInstance().isDisallow());
                        break;
                    case "panel":
                        new ScandiumMenu(player).open(player);
                        break;
                    default:
                        sender.sendMessage(Color.translate(network.getSecondaryColor() + "Usage: /" + network.getMainColor() + label + ChatColor.WHITE + " <debug|disallow|panel>."));
                        break;
                }
            }
        } else {
            player.sendMessage("  ");
            StringUtil.sendCenteredMessage(player, network.getSecondaryColor() + "This server is running " + network.getMainColor() + CorePlugin.getInstance().getConfig().getString("core-settings.name") + network.getSecondaryColor() + ".");
            StringUtil.sendCenteredMessage(player, "&7&oCreated by SolexGames.");
            player.sendMessage("  ");
        }
        return false;
    }
}
