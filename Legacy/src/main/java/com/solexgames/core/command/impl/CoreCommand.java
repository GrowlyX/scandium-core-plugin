package com.solexgames.core.command.impl;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.impl.ScandiumMenu;
import com.solexgames.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

public class CoreCommand extends BukkitCommand {

    public CoreCommand(String name) {
        super(name, "Base Command for " + name + " Core.", "Usage: /" + name + " <debug|disallow|panel>", Collections.singletonList("core"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
            return false;
        }

        Player player = (Player) sender;
        ServerType network = CorePlugin.getInstance().getServerManager().getNetwork();

        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "I'm sorry, but you do not have permission to perform this command.");
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(network.getSecondaryColor() + "Usage: " + network.getMainColor() + "/" + label + ChatColor.WHITE + " <debug|disallow|panel>.");
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
                    sender.sendMessage(network.getSecondaryColor() + "Usage: /" + network.getMainColor() + label + ChatColor.WHITE + " <debug|disallow|panel>.");
                    break;
            }
        }
        return false;
    }
}
