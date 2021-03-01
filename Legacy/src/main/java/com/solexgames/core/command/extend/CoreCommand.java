package com.solexgames.core.command.extend;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.extend.ScandiumMenu;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

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
                switch (args[0]) {
                    case "debug":
                        player.sendMessage(Color.translate((CorePlugin.getInstance().isDebugging() ? "&b[Scandium] &cDisabled debugging." : "&b[Scandium] &aEnabled debugging.")));
                        CorePlugin.getInstance().setDebugging(!CorePlugin.getInstance().isDebugging());
                        break;
                    case "disallow":
                        player.sendMessage(Color.translate((CorePlugin.getInstance().isDisallow() ? "&b[Scandium] &cDisabled disallow." : "&b[Scandium] &aEnabled disallow.")));
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
            StringUtil.sendCenteredMessage(player, "&cI'm sorry, but you do not have permission to perform this command.");
            StringUtil.sendCenteredMessage(player, "&7This server is running " + CorePlugin.getInstance().getConfig().getString("core-settings.name") + ".");
            StringUtil.sendCenteredMessage(player, "&7Created by SolexGames.");
        }
        return false;
    }
}
