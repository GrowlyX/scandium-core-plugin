package com.solexgames.core.command.impl.experience;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.menu.impl.experience.ExperienceMainMenu;
import com.solexgames.core.util.ExperienceUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ExperienceCommand extends BaseCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                new ExperienceMainMenu(player).open(player);
            } else {
                this.sendHelpMessage(sender);
            }
        }

        if (args.length > 0) {
            switch (args[0]) {
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
                                sender.sendMessage(ChatColor.RED + "That is not a valid integer!");
                                return false;
                            }

                            ExperienceUtil.addExperience(target, amount);

                            sender.sendMessage(ChatColor.GREEN + "Added " + ChatColor.WHITE + amount + ChatColor.GREEN + " experience to " + ChatColor.WHITE + target.getDisplayName() + ChatColor.GREEN + "!");
                        } else {
                            sender.sendMessage(ChatColor.RED + "That player does not exist!");
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
                                sender.sendMessage(ChatColor.RED + "That is not a valid integer!");
                                return false;
                            }

                            ExperienceUtil.removeExperience(target, amount);

                            sender.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.WHITE + amount + ChatColor.GREEN + " experience from " + ChatColor.WHITE + target.getDisplayName() + ChatColor.GREEN + "!");
                        } else {
                            sender.sendMessage(ChatColor.RED + "That player does not exist!");
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
                                sender.sendMessage(ChatColor.RED + "That is not a valid integer!");
                                return false;
                            }

                            ExperienceUtil.setExperience(target, amount);

                            sender.sendMessage(ChatColor.GREEN + "Set " + ChatColor.WHITE + amount + ChatColor.GREEN + " experience to " + ChatColor.WHITE + target.getDisplayName() + ChatColor.GREEN + "!");
                        } else {
                            sender.sendMessage(ChatColor.RED + "That player does not exist!");
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
        ServerType serverType = CorePlugin.getInstance().getServerManager().getNetwork();

        sender.sendMessage(Arrays.asList(
                ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52),
                serverType.getMainColor() + ChatColor.BOLD.toString() + "Experience Help:",
                "",
                "/experience " + ChatColor.GRAY + "- Open the Experience Menu.",
                "/experience help " + ChatColor.GRAY + "- View this help message.",
                "/experience amount <player> " + ChatColor.GRAY + "- View the amount of experience a player has.",
                "/experience set <player> <amount> " + ChatColor.GRAY + "- Set a player's experience level to an amount.",
                "/experience add <player> <amount> " + ChatColor.GRAY + "- Add experience to a player.",
                "/experience remove <player> <amount> " + ChatColor.GRAY + "- Remove experience from a player.",
                ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 52)
        ).toArray(new String[0]));
    }
}
