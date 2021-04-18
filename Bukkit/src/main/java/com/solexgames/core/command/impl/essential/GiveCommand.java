package com.solexgames.core.command.impl.essential;

import com.cryptomorin.xseries.XMaterial;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        Player player = (Player) sender;
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        if (args.length < 2) {
            player.sendMessage(Color.translate(serverType.getSecondaryColor() + "Usage: " + serverType.getMainColor() + "/" + label + ChatColor.WHITE + " <player> <amount> <item>."));
        }
        if (args.length > 2) {
            Player target = Bukkit.getPlayer(args[0]);

            int amount;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (Exception ignored) {
                player.sendMessage(ChatColor.RED + ("Error: That amount is not an integer."));
                return false;
            }

            String message = StringUtil.buildMessage(args, 2);

            if (target == null) {
                player.sendMessage(ChatColor.RED + ("Error: That player does not exist."));
                return false;
            }

            String formatted = message.replace(" ", "_").toUpperCase();
            XMaterial material = XMaterial.matchXMaterial(formatted).orElse(null);

            if (material == null) {
                player.sendMessage(ChatColor.RED + ("Error: That material does not exist."));
                return false;
            }

            target.getInventory().addItem(material.parseItem());

            player.sendMessage(ChatColor.GREEN + "You've given " + target.getDisplayName() + ChatColor.GREEN + " " + amount + ChatColor.YELLOW + " " + material.name() + ChatColor.GREEN + "!");
        }

        return false;
    }
}
