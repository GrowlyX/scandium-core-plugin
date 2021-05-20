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

@Command(label = "authsetup", aliases = {"setupauth", "setup2fa"})
public class AuthSetupCommand extends BaseCommand {

    @Override
    public boolean command(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        final Player player = (Player) sender;
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (!player.hasPermission("scandium.2fa")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to setup 2FA.");
            return true;
        }

        CompletableFuture.runAsync(() -> {
            if (potPlayer.getAuthSecret() != null) {
                player.sendMessage(ChatColor.RED + "Error: You've already setup two-factor authentication.");
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
