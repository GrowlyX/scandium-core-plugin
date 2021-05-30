package com.solexgames.core.util.prompt;

import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.DateUtil;
import com.solexgames.core.util.external.impl.grant.GrantReasonPaginatedMenu;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class GrantDurationPrompt extends StringPrompt {

    private final Player granter;

    private final Document target;
    private final Rank rank;
    private final String scope;

    @Override
    public String getPromptText(ConversationContext context) {
        return Color.SECONDARY_COLOR + "Please enter the grant duration for " + Color.MAIN_COLOR + target.getString("name") + Color.SECONDARY_COLOR + ".";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("quit")) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "You've cancelled the granting process.");
        } else if (input.equalsIgnoreCase("perm") || input.equalsIgnoreCase("permanent")) {
            context.getForWhom().sendRawMessage(Color.SECONDARY_COLOR + "Grant duration set to " + ChatColor.DARK_RED + "Permanent" + Color.SECONDARY_COLOR + ".");

            new GrantReasonPaginatedMenu(this.granter, this.target, -1L, this.rank, true, this.scope).openMenu(this.granter);
        } else {
            final long diff = DateUtil.parseDateDiff(input, false);

            if (diff != -1L) {
                new GrantReasonPaginatedMenu(this.granter, this.target, System.currentTimeMillis() - diff, this.rank, false, this.scope).openMenu(this.granter);

                context.getForWhom().sendRawMessage(Color.SECONDARY_COLOR + "Grant duration set to " + Color.MAIN_COLOR + DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - diff, true, true) + Color.SECONDARY_COLOR + ".");
            } else {
                context.getForWhom().sendRawMessage(ChatColor.RED + "That's not a valid duration! Please try again, or type cancel to stop the granting process.");

                return this;
            }
        }

        return Prompt.END_OF_CONVERSATION;
    }
}
