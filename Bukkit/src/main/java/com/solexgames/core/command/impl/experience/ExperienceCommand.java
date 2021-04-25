package com.solexgames.core.command.impl.experience;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.menu.impl.experience.ExperienceMainMenu;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.ExperienceUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExperienceCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                new ExperienceMainMenu(player).open(player);
            } else {
                this.sendHelpMessage(sender);
            }
        }

        // We're checking for permissions during each arg since there may be an argument
        // that allows players without any permissions to execute them.
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "add":
                    if (!sender.hasPermission("scandium.experience.management")) {
                        sender.sendMessage(NO_PERMISSION);
                        return false;
                    }
                    if (args.length < 3) {
                        this.sendHelpMessage(sender);
                    }
                    if (args.length == 3) {
                        final Player target = Bukkit.getPlayerExact(args[1]);
                        int amount;

                        if (target != null) {
                            try {
                                amount = Integer.parseInt(args[2]);
                            } catch (NumberFormatException exception) {
                                sender.sendMessage(ChatColor.RED + "Error: That is not a valid integer!");
                                return false;
                            }

                            ExperienceUtil.addExperience(target, amount);

                            sender.sendMessage(ChatColor.GREEN + "Added " + ChatColor.WHITE + amount + ChatColor.GREEN + " experience to " + ChatColor.WHITE + target.getDisplayName() + ChatColor.GREEN + "!");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Error: That player does not exist!");
                        }
                    }
                    break;
                case "remove":
                    if (!sender.hasPermission("scandium.experience.management")) {
                        sender.sendMessage(NO_PERMISSION);
                        return false;
                    }

                    if (args.length < 3) {
                        this.sendHelpMessage(sender);
                    }
                    if (args.length == 3) {
                        final Player target = Bukkit.getPlayerExact(args[1]);
                        int amount;

                        if (target != null) {
                            try {
                                amount = Integer.parseInt(args[2]);
                            } catch (NumberFormatException exception) {
                                sender.sendMessage(ChatColor.RED + "Error: That is not a valid integer!");
                                return false;
                            }

                            ExperienceUtil.removeExperience(target, amount);

                            sender.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.WHITE + amount + ChatColor.GREEN + " experience from " + ChatColor.WHITE + target.getDisplayName() + ChatColor.GREEN + "!");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Error: That player does not exist!");
                        }
                    }
                    break;
                case "set":
                    if (!sender.hasPermission("scandium.experience.management")) {
                        sender.sendMessage(NO_PERMISSION);
                        return false;
                    }

                    if (args.length < 3) {
                        this.sendHelpMessage(sender);
                    }
                    if (args.length == 3) {
                        final Player target = Bukkit.getPlayerExact(args[1]);
                        int amount;

                        if (target != null) {
                            try {
                                amount = Integer.parseInt(args[2]);
                            } catch (NumberFormatException exception) {
                                sender.sendMessage(ChatColor.RED + "Error: That is not a valid integer!");
                                return false;
                            }

                            ExperienceUtil.setExperience(target, amount);

                            sender.sendMessage(ChatColor.GREEN + "Set " + ChatColor.WHITE + amount + ChatColor.GREEN + " experience to " + ChatColor.WHITE + target.getDisplayName() + ChatColor.GREEN + "!");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Error: That player does not exist!");
                        }
                    }
                    break;
                case "help":
                default:
                    this.sendHelpMessage(sender);
                    break;
            }
        }
        return false;
    }

    public void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(this.getHelpMessage(
                "/experience",
                "/experience help",
                "/experience amount <player>",
                "/experience set <player> <amount>",
                "/experience add <player> <amount>",
                "/experience remove <player> <amount>"
        ));
    }
}
