package com.solexgames.core.command.impl.auth;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.command.BaseCommand;
import com.solexgames.core.command.annotation.Command;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.util.prompt.DisclaimerPrompt;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Command(label = "authsetup", permission = "scandium.2fa", aliases = {"setupauth", "setup2fa"})
public class AuthSetupCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.ONLY_PLAYERS);
            return true;
        }

        final Player player = (Player) sender;
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        CompletableFuture.runAsync(() -> {
            if (potPlayer.isHasSetup2FA() || potPlayer.getAuthSecret() != null) {
                player.sendMessage(ChatColor.RED + "You've already setup two-factor authentication.");
                player.sendMessage(ChatColor.RED + "Contact a developer or management member to reset your 2FA.");
                return;
            }

            final ConversationFactory factory = CorePlugin.getInstance().getConversationFactory()
                    .withFirstPrompt(new DisclaimerPrompt())
                    .withLocalEcho(false)
                    .thatExcludesNonPlayersWithMessage(this.ONLY_PLAYERS);

            player.beginConversation(factory.buildConversation(player));
        });

        return true;
    }
}
