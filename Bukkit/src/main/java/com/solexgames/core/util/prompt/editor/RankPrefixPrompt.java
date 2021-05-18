package com.solexgames.core.util.prompt.editor;

import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.external.impl.editor.RankEditorEditMenu;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

/**
 * @author GrowlyX
 * @since 5/18/2021
 */

@RequiredArgsConstructor
public class RankPrefixPrompt extends StringPrompt {

    private final Player player;
    private final Rank rank;

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return Color.SECONDARY_COLOR + "Please enter the rank prefix you want to set. " + ChatColor.GRAY + "(Or, type 'cancel' to cancel)";
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        if (s.equalsIgnoreCase("cancel")) {
            conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "You've exited the rank editor.");

            return END_OF_CONVERSATION;
        }

        final String prefix = Color.translate(s);

        this.rank.setPrefix(prefix);

        conversationContext.getForWhom().sendRawMessage(Color.SECONDARY_COLOR + "You've set the prefix to: " + prefix);

        new RankEditorEditMenu(this.rank).openMenu(this.player);

        return END_OF_CONVERSATION;
    }
}
