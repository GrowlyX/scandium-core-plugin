package com.solexgames.core.util.prompt;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class DisclaimerPrompt extends StringPrompt {

    @Override
    public String getPromptText(ConversationContext context) {
        return ChatColor.GRAY + "Would you like to setup Two Factor Authentication? If yes, please type \"yes\" in chat, otherwise, type anything else.";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (input.equalsIgnoreCase("yes")) {
            return new MapScannerPrompt();
        }

        context.getForWhom().sendRawMessage(ChatColor.GREEN + "You've aborted the 2FA Setup.");

        return Prompt.END_OF_CONVERSATION;
    }
}
