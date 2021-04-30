package com.solexgames.core.util.prompt;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.menu.impl.grant.GrantSelectConfirmMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.media.MediaConstants;
import com.solexgames.core.util.Color;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;

@RequiredArgsConstructor
public class SocialMediaPrompt extends StringPrompt {

    private final Player player;

    private final String mediaName;
    private final String mediaFancyName;

    @Override
    public String getPromptText(ConversationContext context) {
        return Color.SECONDARY_COLOR + "Please enter your " + this.mediaFancyName + Color.SECONDARY_COLOR + "!";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("quit")) {
            context.getForWhom().sendRawMessage(ChatColor.RED + ("Cancelled the social media editing process."));
        } else {
            final boolean matches = this.matches(input);

            if (matches) {
                this.runAfterMatch(input);

                context.getForWhom().sendRawMessage(Color.SECONDARY_COLOR + "You've updated your " + this.mediaFancyName + Color.SECONDARY_COLOR + " to " + Color.MAIN_COLOR + input + Color.SECONDARY_COLOR + ".");
            } else {
                context.getForWhom().sendRawMessage(ChatColor.RED + "That's not a valid value for the social media account!");
                context.getForWhom().sendRawMessage(ChatColor.RED + "Please try again or type \"cancel\" to exit the editor.");

                return this;
            }
        }

        return Prompt.END_OF_CONVERSATION;
    }

    private boolean matches(String input) {
        Matcher mediaMatcher = null;

        switch (this.mediaName.toLowerCase()) {
            case "discord":
                mediaMatcher = MediaConstants.DISCORD_USERNAME_REGEX.matcher(input);
                break;
            case "twitter":
                mediaMatcher = MediaConstants.TWITTER_USERNAME_REGEX.matcher(input);
                break;
            case "instagram":
                mediaMatcher = MediaConstants.INSTAGRAM_USERNAME_REGEX.matcher(input);
                break;
            case "youtube":
                mediaMatcher = MediaConstants.YOUTUBE_PROFILE_LINK_REGEX.matcher(input);
                break;
        }

        return mediaMatcher != null && mediaMatcher.matches();
    }

    private void runAfterMatch(String input) {
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(this.player);

        switch (this.mediaName.toLowerCase()) {
            case "discord":
                potPlayer.getMedia().setDiscord(input);
                break;
            case "twitter":
                potPlayer.getMedia().setTwitter(input);
                break;
            case "instagram":
                potPlayer.getMedia().setInstagram(input);
                break;
            case "youtube":
                potPlayer.getMedia().setYoutubeLink(input);
                break;
        }
    }
}
