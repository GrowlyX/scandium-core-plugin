package com.solexgames.core.util.command;

import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.util.Color;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;

import java.util.Set;

public class CustomHelpTopic extends HelpTopic {

    public CustomHelpTopic(BaseCommand baseCommand, Set<String> aliases) {
        this.name = "/" + baseCommand.getName();
        this.shortText = "";

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(Color.SECONDARY_COLOR);
        stringBuilder.append("Description: ");
        stringBuilder.append(Color.MAIN_COLOR);
        stringBuilder.append("No description set for this command.");
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
        return commandSender.hasPermission("scandium.staff");
    }
}
