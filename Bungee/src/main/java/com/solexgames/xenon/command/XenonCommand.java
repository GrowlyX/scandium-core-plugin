package com.solexgames.xenon.command;

import com.solexgames.xenon.CorePlugin;
import com.solexgames.xenon.util.Color;
import com.solexgames.xenon.util.StringUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 * @author GrowlyX
 * @since 3/5/2021
 */

public class XenonCommand extends Command {

    public XenonCommand() {
        super("xenon", null, "bungee", "proxy");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessages(
                    Color.translate("&bThis proxy is running &3&lXenon&b."),
                    Color.translate("&7Created by SolexGames.")
            );
        }

        if (args.length > 0) {
            if (sender.hasPermission("xenon.network.motd")) {
                switch (args[0].toLowerCase()) {
                    case "normalmotd":
                        if (args.length == 1) {
                            sender.sendMessage(Color.translate("&3Usage: &b/xenon &f<normalmotd|maintenancemotd> <message>..."));
                        }
                        if (args.length > 1) {
                            String message = Color.translate(StringUtil.buildMessage(args, 1));

                            CorePlugin.getInstance().setNormalMotd(message.replace("<bar>", Character.toString('⎜')).replace("<nl>", "\n"));
                            CorePlugin.getInstance().getConfiguration().set("motd.normal", message);

                            sender.sendMessage(Color.translate("  "));
                            sender.sendMessage(Color.translate("&a&lSet the Normal MOTD to:"));
                            sender.sendMessage(Color.translate(CorePlugin.getInstance().getNormalMotd()));
                            sender.sendMessage(Color.translate("  "));
                        }
                        break;
                    case "maintenancemotd":
                        if (args.length == 1) {
                            sender.sendMessage(Color.translate("&3Usage: &b/xenon &f<normalmotd|maintenancemotd> <message>..."));
                        }
                        if (args.length > 1) {
                            String message = Color.translate(StringUtil.buildMessage(args, 1));

                            CorePlugin.getInstance().setMaintenanceMotd(message.replace("<bar>", Character.toString('⎜')).replace("<nl>", "\n"));
                            CorePlugin.getInstance().getConfiguration().set("motd.maintenance", message);

                            sender.sendMessage(Color.translate("  "));
                            sender.sendMessage(Color.translate("&c&lSet the Maintenance MOTD to:"));
                            sender.sendMessage(Color.translate(CorePlugin.getInstance().getNormalMotd()));
                            sender.sendMessage(Color.translate("  "));
                        }
                        break;
                    default:
                        sender.sendMessage(Color.translate("&3Usage: &b/xenon &f<normalmotd|maintenancemotd> <message>..."));
                        break;
                }
            } else {
                sender.sendMessage(Color.translate("&cI'm sorry, but you do not have permission to perform this command."));
            }
        }
    }
}
