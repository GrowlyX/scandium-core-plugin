package me.growlyx.core.essentials.commands;

import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.annotation.command.Commands;

@Commands(@org.bukkit.plugin.java.annotation.command.Command(name = "clear", permission = "core.commands.clear"))
public class ClearCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] string) {

        String[] args = string;
        Player player = (Player) sender;

        if (sender instanceof Player) {

            if (args.length == 0) {

                player.getInventory().setContents(new ItemStack[36]);
                player.getInventory().setArmorContents(new ItemStack[4]);
                player.updateInventory();
                player.sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.CLEAR")));

                return true;
            }

            if (args.length == 1) {

                Player clear = Bukkit.getPlayer(args[0]);

                clear.getInventory().setContents(new ItemStack[36]);
                clear.getInventory().setArmorContents(new ItemStack[4]);
                clear.updateInventory();
                clear.sendMessage(CC.translate("&aYour inventory has been cleared by &f" + sender.getName()));

                return true;
            }

        } else {

            System.out.println("Only players can execute this command.");

        }

        return false;
    }
}