package me.growlyx.core.chat;

import me.growlyx.core.Core;
import me.growlyx.core.chat.commands.ChatCommand;
import me.growlyx.core.utils.CC;
import me.growlyx.core.utils.configurations.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static me.growlyx.core.chat.Chat.*;

public class ChatListener implements Listener {

    public static boolean chatEnabled = true;

    long Time;

    @EventHandler
    public void GlobalMute(AsyncPlayerChatEvent gm) {
        if (Messages.aboolean("BOOLEAN.CHAT-MUTED")) {
            if (!gm.getPlayer().hasPermission("core.chat.mute.bypass")) {
                gm.setCancelled(true);
                gm.getPlayer().sendMessage(CC.translate(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.CHAT.MUTED")));
            }
        }
    }

    @EventHandler
    public void playerchat(AsyncPlayerChatEvent e) {
        String[] arr$ = e.getMessage().split(" ");
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String word = arr$[i$];
            if (Core.getPlugin(Core.class).getConfig().getStringList("MESSAGES.CHAT.BANNED-WORDS").contains(word)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Messages.string("FORMAT.PREFIX") + Messages.string("MESSAGES.CHAT.BANNED-WORDS-MSG"));
            }
        }

    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.getRecipients().removeAll(playersMuted);
    }

    @EventHandler
    public void SpamChat(AsyncPlayerChatEvent e) {

        Player player = e.getPlayer();

        this.Time = Messages.integer("MESSAGES.CHAT.ANTI-SPAM-TIME");
        this.Time *= 20L;

        boolean spam = spammers.contains(player.getName());
        if (!player.hasPermission("core.antispam.bypass")) {
            if (!spam) {
                spammers.add(player.getName());
            } else {
                e.setCancelled(true);
                player.sendMessage(CC.translate(Messages.string("MESSAGES.CHAT.SPAM-MSG")));
            }
        }

    }
}
