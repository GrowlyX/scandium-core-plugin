package com.solexgames.core.command.extend;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.extend.ScandiumMenu;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoreCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
                    case "type":
                        if (args.length == 1) {
                            player.sendMessage(Color.translate("&cUsage: /" + label + " type <type>"));
                        }
                        if (args.length == 2) {
                            String type = args[1];
                            try {
                                ServerType serverType = ServerType.valueOf(type);
                                CorePlugin.getInstance().getServerManager().setNetwork(serverType);
                                player.sendMessage(Color.translate("&aSet the server type to " + serverType.getServerName()));
                            } catch (Exception e) {
                                player.sendMessage(Color.translate("&cThat's not a valid server type."));
                                player.sendMessage(Color.translate("&cTypes:"));
                                for (ServerType value : ServerType.values()) {
                                    player.sendMessage(Color.translate(" &7* &b" + value));
                                }
                            }
                        }
                        break;
                    default:
                        player.sendMessage(Color.translate("&cUsage: /" + label + " <debug|disallow|panel>"));
                        break;
                }
            }
        } else {
            StringUtil.sendCenteredMessage(player, "&cI'm sorry, but you do not have permission to perform this command.");
        }
        return false;
    }
}
