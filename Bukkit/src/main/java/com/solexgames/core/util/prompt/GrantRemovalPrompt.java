package com.solexgames.core.util.prompt;

import com.solexgames.core.menu.impl.grant.GrantRemoveConfirmMenu;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.util.Color;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class GrantRemovalPrompt extends StringPrompt {

    private final Grant grant;
    private final Player remover;
    private final Player target;

    @Override
    public String getPromptText(ConversationContext context) {
        return Color.SECONDARY_COLOR + "Please enter the grant removal reason for " + target.getDisplayName() + Color.SECONDARY_COLOR + ".";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        new GrantRemoveConfirmMenu(this.remover, this.target, this.grant, input).open(this.remover);

        this.remover.sendMessage(Color.SECONDARY_COLOR + "You've set the removal reason to:");
        this.remover.sendMessage(Color.MAIN_COLOR + input);

        return Prompt.END_OF_CONVERSATION;
    }
}
