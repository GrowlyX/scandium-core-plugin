package com.solexgames.core.command.extend;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.extend.ScandiumMenu;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
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
            sender.sendMessage("die");
            return false;
        }

        Player player = (Player) sender;
        if (player.isOp()) {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + label + " <debug|disallow|panel>"));
            }
            if (args.length > 0) {
                ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

                switch (args[0]) {
                    case "debug":
                        player.sendMessage(Color.translate((CorePlugin.getInstance().isDebugging() ? serverType.getMainColor() + "[" + CorePlugin.getInstance().getConfig().getString("core-settings.name") + "] &cDisabled debugging." : serverType.getMainColor() + "[" + CorePlugin.getInstance().getConfig().getString("core-settings.name") + "] &aEnabled debugging.")));
                        CorePlugin.getInstance().setDebugging(!CorePlugin.getInstance().isDebugging());
                        break;
                    case "disallow":
                        player.sendMessage(Color.translate((CorePlugin.getInstance().isDisallow() ? serverType.getMainColor() + "[" + CorePlugin.getInstance().getConfig().getString("core-settings.name") + "] &cDisabled disallow." : serverType.getMainColor() + "[" + CorePlugin.getInstance().getConfig().getString("core-settings.name") + "] &aEnabled disallow.")));
                        CorePlugin.getInstance().setDisallow(!CorePlugin.getInstance().isDisallow());
                        break;
                    case "panel":
                        new ScandiumMenu(player).open(player);
                        break;
                    default:
                        player.sendMessage(Color.translate("&cUsage: /" + label + " <debug|disallow|panel>"));
                        break;
                }
            }
        } else {
            ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();
            player.sendMessage("  ");
            StringUtil.sendCenteredMessage(player, serverType.getSecondaryColor() + "This server is running " + serverType.getMainColor() + CorePlugin.getInstance().getConfig().getString("core-settings.name") + serverType.getSecondaryColor() + ".");
            StringUtil.sendCenteredMessage(player, "&7&oCreated by SolexGames.");
            player.sendMessage("  ");
        }
        return false;
    }
}
