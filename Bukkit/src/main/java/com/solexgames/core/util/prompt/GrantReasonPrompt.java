package com.solexgames.core.util.prompt;

import com.solexgames.core.menu.impl.grant.GrantRemoveConfirmMenu;
import com.solexgames.core.menu.impl.grant.GrantSelectConfirmMenu;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class GrantReasonPrompt extends StringPrompt {

    private final Player granter;

    private final Document target;
    private final Rank rank;
    private final long duration;
    private final String scope;
    private final boolean permanent;

    @Override
    public String getPromptText(ConversationContext context) {
        return Color.SECONDARY_COLOR + "Please enter the grant reason for " + Color.MAIN_COLOR + target.getString("name") + Color.SECONDARY_COLOR + ".";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("quit")) {
            context.getForWhom().sendRawMessage(ChatColor.RED + ("Cancelled the granting process."));
        } else {
            context.getForWhom().sendRawMessage(Color.SECONDARY_COLOR + "Grant reason set to " + Color.MAIN_COLOR + input + Color.SECONDARY_COLOR + ".");

            new GrantSelectConfirmMenu(this.granter, this.target, this.rank, this.duration, input, this.permanent, this.scope).open(this.granter);
        }

        return Prompt.END_OF_CONVERSATION;
    }
}
