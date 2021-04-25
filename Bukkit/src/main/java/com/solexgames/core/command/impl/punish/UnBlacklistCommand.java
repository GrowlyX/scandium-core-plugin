package com.solexgames.core.command.impl.punish;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class UnBlacklistCommand extends BaseCommand {

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("scandium.command.unblacklist")) {
            sender.sendMessage(NO_PERMISSION);
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage(Color.translate(Color.SECONDARY_COLOR + "Usage: " + Color.MAIN_COLOR + "/" + label + ChatColor.WHITE + " <player> <reason> &7[-s]."));
        }
        if (args.length >= 2) {
            final Document document = CorePlugin.getInstance().getPlayerManager().getDocumentByName(args[0]).orElse(null);
            final String message = StringUtil.buildMessage(args, 1);

            if (document != null) {
                CorePlugin.getInstance().getPunishmentManager().handleUnPunishment(document, message, (sender instanceof Player ? (Player) sender : null), PunishmentType.BLACKLIST, true);
            } else {
                sender.sendMessage(ChatColor.RED + "Error: That player does not exist in our database.");
            }
        }

        return false;
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }
}
