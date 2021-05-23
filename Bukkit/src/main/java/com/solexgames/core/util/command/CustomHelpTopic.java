package com.solexgames.core.util.command;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;

import java.util.Set;

public class CustomHelpTopic extends HelpTopic {

    private final BaseCommand baseCommand;

    public CustomHelpTopic(BaseCommand baseCommand, Set<String> aliases) {
        this.baseCommand = baseCommand;
        this.name = "/" + baseCommand.getName();
        this.shortText = "";

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(Color.SECONDARY_COLOR);
        stringBuilder.append("Label: ");
        stringBuilder.append(Color.MAIN_COLOR);
        stringBuilder.append(this.name);
        stringBuilder.append("\n");

        stringBuilder.append(Color.SECONDARY_COLOR);
        stringBuilder.append("Description: ");
        stringBuilder.append(Color.MAIN_COLOR);
        stringBuilder.append(StringUtils.capitalize(baseCommand.getName()));
        stringBuilder.append(" command.");
        stringBuilder.append("\n");

        stringBuilder.append(Color.SECONDARY_COLOR);
        stringBuilder.append("Usage: ");
        stringBuilder.append(Color.MAIN_COLOR);
        stringBuilder.append(this.name);

        if (aliases != null && aliases.size() > 0) {
            stringBuilder.append("\n");
            stringBuilder.append(Color.SECONDARY_COLOR);
            stringBuilder.append("Aliases: ");
            stringBuilder.append(Color.MAIN_COLOR);
            stringBuilder.append(StringUtils.join(aliases, ChatColor.WHITE + ", " + Color.MAIN_COLOR));
        }

        this.fullText = stringBuilder.toString();
    }

    @Override
    public boolean canSee(CommandSender commandSender) {
        return baseCommand.getPermissionNode() == null || baseCommand.getPermissionNode().equals("") || commandSender.hasPermission(baseCommand.getPermissionNode());
    }
}
