package com.solexgames.core.command.impl.essential;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Command(label = "permissions", permission = "scandium.command.permissions", aliases = {"perms"})
public class PermissionsCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYERS);
            return false;
        }

        final Player player = (Player) sender;

        final List<String> permissions = player.getEffectivePermissions().stream()
                .map(PermissionAttachmentInfo::getPermission)
                .collect(Collectors.toList());
        final int permissionsCount = permissions.size();

        player.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 53));
        StringUtil.sendCenteredMessage(player, Color.SECONDARY_COLOR + "You currently have " + Color.MAIN_COLOR + "" + permissionsCount + Color.SECONDARY_COLOR + " permissions!");
        player.sendMessage("  ");
        permissions.forEach(s -> player.sendMessage(ChatColor.GRAY + " * " + Color.MAIN_COLOR + s));
        player.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 53));

        return false;
    }
}
