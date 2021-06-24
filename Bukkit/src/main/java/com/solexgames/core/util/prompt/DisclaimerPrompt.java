package com.solexgames.core.util.prompt;

import com.solexgames.core.util.Color;
import com.solexgames.core.util.Constants;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.util.Arrays;
import java.util.List;

public class DisclaimerPrompt extends StringPrompt {

    private final List<String> acceptedResponses = Arrays.asList(
            "yes", "yea", "ye",
            "yse", "ys", "ya",
            "mhm", "YES", "YE",
            "yeh", "Yes", "YEH"
    );

    @Override
    public String getPromptText(ConversationContext context) {
        return Constants.STAFF_PREFIX + Color.SECONDARY_COLOR + "Would you like to setup two-factor authentication? If yes, please type " + ChatColor.GREEN + "\"yes\"" + Color.SECONDARY_COLOR + " in chat, otherwise, type anything else.";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (this.acceptedResponses.contains(input)) {
            return new MapScannerPrompt();
        }

        context.getForWhom().sendRawMessage(Constants.STAFF_PREFIX + ChatColor.RED + " You've aborted the two-factor authentication setup process.");

        return Prompt.END_OF_CONVERSATION;
    }
}
