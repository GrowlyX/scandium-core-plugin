package me.growlyx.core.essentials.commands;

import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "more", permission = "core.commands.more"))
public class MoreCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] string) {

        String[] args = string;
        Player player = (Player) sender;

        if (sender instanceof Player) {

            if (args.length == 0) {

                if (player.getItemInHand() == null) {
                    player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + "&cThere is nothing in your hand!"));
                }

                player.getItemInHand().setAmount(64);
                player.updateInventory();
                player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + "&aYou have been given more of what you have in your hand."));
            }

        } else {

            System.out.println("Only players can execute this command.");

        }

        return false;
    }
}