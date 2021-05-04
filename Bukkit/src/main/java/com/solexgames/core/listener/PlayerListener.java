package com.solexgames.core.listener;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.manager.ServerManager;
import com.solexgames.core.menu.IMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.punishment.PunishmentStrings;
import com.solexgames.core.server.NetworkServer;
import com.solexgames.core.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author GrowlyX
 * @since 2021
 */

public class PlayerListener implements Listener {

    public ServerManager serverManager = CorePlugin.getInstance().getServerManager();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!CorePlugin.getInstance().getServerSettings().isCanJoin()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, PunishmentStrings.SERVER_NOT_LOADED);
            return;
        }

        CorePlugin.getInstance().getPlayerManager().setupPlayer(event);

        event.allow();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPreLoginCheck(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getUniqueId());
            final boolean isHub = CorePlugin.getInstance().getServerName().toLowerCase().contains("hub") || CorePlugin.getInstance().getServerName().toLowerCase().contains("lobby");

            if (potPlayer != null) {
                if (potPlayer.isCurrentlyRestricted() || potPlayer.isCurrentlyBlacklisted() && !isHub) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, potPlayer.getRestrictionMessage());
                } else if (!potPlayer.findIpRelative(event, isHub)) {
                    event.allow();
                }
            } else {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, PunishmentStrings.PLAYER_DATA_LOAD);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer());

        if (potPlayer == null) {
            event.getPlayer().kickPlayer(PunishmentStrings.PLAYER_DATA_LOAD);
            return;
        }

        CompletableFuture.runAsync(() -> {
            potPlayer.onAfterDataLoad();

            Bukkit.getScheduler().runTaskLaterAsynchronously(CorePlugin.getInstance(), () -> Bukkit.getOnlinePlayers().stream()
                    .map(player -> CorePlugin.getInstance().getPlayerManager().getPlayer(player))
                    .filter(potPlayer1 -> potPlayer1.isVanished() && potPlayer.getActiveGrant().getRank().getWeight() < potPlayer1.getActiveGrant().getRank().getWeight())
                    .forEach(player -> event.getPlayer().hidePlayer(player.getPlayer())), 35L);

            if (this.serverManager.isClearChatJoin()) {
                for (int lines = 0; lines < 250; lines++) {
                    event.getPlayer().sendMessage("  ");
                }
            }

            if (this.serverManager.isJoinMessageEnabled()) {
                if (this.serverManager.isJoinMessageCentered()) {
                    this.serverManager.getJoinMessage().forEach(s -> event.getPlayer().sendMessage(Color.translate(s.replace("%PLAYER%", event.getPlayer().getDisplayName()))));
                } else {
                    StringUtil.sendCenteredMessage(event.getPlayer(), (ArrayList<String>) this.serverManager.getJoinMessage());
                }
            }

            if (event.getPlayer().hasPermission("scandium.staff")) {
                CorePlugin.getInstance().getServerManager().getStaffInformation().forEach(s -> event.getPlayer().sendMessage(s
                        .replace("<nice_char>", Character.toString('»'))
                        .replace("<channel>", ChatColor.RED + "None")
                        .replace("<messages>", (potPlayer.isCanSeeStaffMessages() ? ChatColor.GREEN + "Shown" : ChatColor.RED + "Hidden"))
                        .replace("<filter>", (potPlayer.isCanSeeFiltered() ? ChatColor.GREEN + "Shown" : ChatColor.RED + "Hidden"))
                        .replace("<modmode>", (potPlayer.isStaffMode() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"))
                        .replace("<vanish>", (potPlayer.isVanished() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"))
                ));
            }

            if (potPlayer.isAutoVanish()) {
                potPlayer.getPlayer().sendMessage(Color.translate(CorePlugin.getInstance().getServerManager().getAutomaticallyPutInto().replace("<value>", "vanish")));

                CorePlugin.getInstance().getPlayerManager().vanishPlayerRaw(potPlayer.getPlayer());
            }

            if (potPlayer.isAutoModMode()) {
                potPlayer.getPlayer().sendMessage(Color.translate(CorePlugin.getInstance().getServerManager().getAutomaticallyPutInto().replace("<value>", "mod mode")));

                CorePlugin.getInstance().getPlayerManager().modModeRaw(potPlayer.getPlayer());
            }

            if (potPlayer.isCurrentlyRestricted()) {
                event.getPlayer().sendMessage(potPlayer.getRestrictionMessage());
            }

            if (potPlayer.isHasActiveWarning()) {
                event.getPlayer().sendMessage(potPlayer.getWarningMessage());
            }

            if (potPlayer.getDisguiseRank() != null) {
                event.getPlayer().sendMessage(Color.SECONDARY_COLOR + "You've been automatically disguised as " + potPlayer.getColorByRankColor() + potPlayer.getDisguiseRank().getName() + Color.SECONDARY_COLOR + "!");
            }
        });

        CorePlugin.getInstance().getNMS().setupTablist(event.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer());

        if (potPlayer != null && potPlayer.isFrozen()) {
            event.setCancelled(true);
            event.getPlayer().teleport(event.getFrom());
        }
    }

    @EventHandler
    public void onDamaged(EntityDamageEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER)) {
            final Player player = (Player) event.getEntity();
            final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

            if (potPlayer != null && potPlayer.isFrozen()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) throws IOException, ParseException {
        if (event.getInventory().getHolder() instanceof IMenu) {
            ((IMenu) event.getInventory().getHolder()).onInventoryClick(event);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof IMenu) {
            ((IMenu) event.getInventory().getHolder()).onInventoryDrag(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof IMenu) {
            ((IMenu) event.getInventory().getHolder()).onInventoryClose(event);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        final String message = event.getMessage();

        if (potPlayer.isCurrentlyRestricted()) {
            player.sendMessage(ChatColor.RED + "You cannot chat as you are currently restricted.");

            event.setCancelled(true);
            return;
        }

        if (LockedState.isLocked(player)) {
            event.setCancelled(true);

            player.sendMessage(ChatColor.RED + "You cannot perform this action right now!");
            player.sendMessage(ChatColor.RED + "The only action you can perform is " + ChatColor.DARK_RED + "/2fa" + ChatColor.RED + "!");

            return;
        }

        final boolean filtered = CorePlugin.getInstance().getFilterManager().isMessageFiltered(player, message);
        if (filtered) {
            if (!player.hasPermission("scandium.filter.bypass")) {
                player.sendMessage(ChatColor.RED + "That message has been filtered as it has a blocked term in it.");

                event.setCancelled(true);
            }
        }

        if (potPlayer.isFrozen()) {
            PlayerUtil.sendToStaff("&c[Frozen] &f" + potPlayer.getPlayer().getDisplayName() + "&7: &e" + event.getMessage());
            player.sendMessage(Color.translate("&c[Frozen] &f" + potPlayer.getPlayer().getDisplayName() + "&7: &e") + event.getMessage());

            event.setCancelled(true);

            return;
        }

        if (potPlayer.getChannel() != null) {
            RedisUtil.publishAsync(RedisUtil.onChatChannel(potPlayer.getChannel(), message, player));

            event.setCancelled(true);
            return;
        }

        if (player.hasMetadata("spectator")) {
            event.setCancelled(true);
            return;
        }

        if (CorePlugin.getInstance().getServerManager().isChatEnabled() || player.hasPermission("scandium.chat.bypass")) {
            if (potPlayer.isCurrentlyMuted()) {
                player.sendMessage(PunishmentStrings.MUTE_MESSAGE);
                event.setCancelled(true);

                return;
            }

            if (CorePlugin.getInstance().getServerSettings().isChatFormatEnabled()) {
                this.checkChannel(event, player, potPlayer);
            }
        } else {
            player.sendMessage(ChatColor.RED + "The chat is currently muted. Please try chatting again later.");

            event.setCancelled(true);
        }
    }

    private void checkChannel(AsyncPlayerChatEvent event, Player player, PotPlayer potPlayer) {
        if (event.getMessage().startsWith("! ") && player.hasPermission(ChatChannelType.STAFF.getPermission())) {
            event.setCancelled(true);

            RedisUtil.publishAsync(RedisUtil.onChatChannel(ChatChannelType.STAFF, event.getMessage().replace("! ", ""), event.getPlayer()));
        } else if (event.getMessage().startsWith("# ") && player.hasPermission(ChatChannelType.ADMIN.getPermission())) {
            event.setCancelled(true);

            RedisUtil.publishAsync(RedisUtil.onChatChannel(ChatChannelType.ADMIN, event.getMessage().replace("# ", ""), event.getPlayer()));
        } else if (event.getMessage().startsWith("$ ") && player.hasPermission(ChatChannelType.DEV.getPermission())) {
            event.setCancelled(true);

            RedisUtil.publishAsync(RedisUtil.onChatChannel(ChatChannelType.DEV, event.getMessage().replace("$ ", ""), event.getPlayer()));
        } else if (event.getMessage().startsWith("@ ") && player.hasPermission(ChatChannelType.HOST.getPermission())) {
            event.setCancelled(true);

            RedisUtil.publishAsync(RedisUtil.onChatChannel(ChatChannelType.HOST, event.getMessage().replace("@ ", ""), event.getPlayer()));
        } else {
            final long slowChat = CorePlugin.getInstance().getServerManager().getChatSlow();

            if ((System.currentTimeMillis() < potPlayer.getChatCooldown())) {
                if (player.hasPermission("scandium.chat.cooldown.bypass")) {
                    this.checkThenSend(event, player, potPlayer, slowChat);
                } else {
                    player.sendMessage(slowChat > 0L ? Color.translate(PunishmentStrings.SLOW_CHAT_MESSAGE.replace("<amount>", DurationFormatUtils.formatDurationWords(slowChat, true, true))) : Color.translate(PunishmentStrings.COOL_DOWN_MESSAGE));
                    event.setCancelled(true);
                }
            } else {
                this.checkThenSend(event, player, potPlayer, slowChat);
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer());
        final Player player = event.getPlayer();

        if (potPlayer == null) {
            event.setCancelled(true);
            return;
        }

        if (LockedState.isLocked(event.getPlayer())) {
            player.sendMessage(ChatColor.RED + "You cannot perform this action right now!");
            player.sendMessage(ChatColor.RED + "The only action you can perform is " + ChatColor.RED + ChatColor.BOLD.toString() + "/2fa" + ChatColor.RED + "!");

            event.setCancelled(true);
        }

        if (potPlayer.isFrozen()) {
            event.setCancelled(true);
            return;
        }

        if (potPlayer.isCurrentlyRestricted() && !event.getMessage().startsWith("/discord")) {
            player.sendMessage(ChatColor.RED + "You cannot perform this command as you are currently banned.");
            player.sendMessage(ChatColor.RED + "The only command you can perform is " + ChatColor.RED + ChatColor.BOLD.toString() + "/discord" + ChatColor.RED + "!");

            event.setCancelled(true);
            return;
        }

        if (event.getMessage().contains(":") && !event.getPlayer().isOp()) {
            player.sendMessage(ChatColor.RED + "You cannot execute commands with semi-colons.");

            event.setCancelled(true);
        }

        final long commandCoolDown = 1L;
        if (System.currentTimeMillis() < potPlayer.getCommandCooldown()) {
            if (!event.getPlayer().hasPermission("scandium.command.cooldown.bypass")) {
                event.getPlayer().sendMessage(Color.translate(PunishmentStrings.CMD_CHAT_MESSAGE.replace("<amount>", DurationFormatUtils.formatDurationWords(commandCoolDown, true, true))));
                event.setCancelled(true);
            }
        }

        if (CorePlugin.getInstance().getServerSettings().isAntiCommandSpamEnabled()) {
            potPlayer.setCommandCooldown(System.currentTimeMillis() + 1L);
        } else {
            potPlayer.setCommandCooldown(0L);
        }
    }

    private void checkThenSend(AsyncPlayerChatEvent event, Player player, PotPlayer potPlayer, long slowChat) {
        Bukkit.getOnlinePlayers().stream()
                .map(player1 -> CorePlugin.getInstance().getPlayerManager().getPlayer(player1))
                .filter(potPlayer1 -> potPlayer1 != null && potPlayer1.isIgnoring(potPlayer.getPlayer()) && potPlayer.isCanSeeGlobalChat())
                .forEach(potPlayer1 -> potPlayer1.getPlayer().sendMessage(CorePlugin.getInstance().getServerManager().getChatFormat().getFormatted(player, potPlayer1.getPlayer(), event.getMessage())));

        if (CorePlugin.getInstance().getServerSettings().isAntiSpamEnabled()) {
            potPlayer.setChatCooldown(System.currentTimeMillis() + (slowChat > 0L ? slowChat : 3000L));
        } else {
            potPlayer.setChatCooldown(0L);
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        Player player = event.getPlayer();

        if (LockedState.isLocked(player)) {
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack.getType() == Material.MAP && itemStack.getItemMeta().hasLore()) {
                    final List<String> lore = itemStack.getItemMeta().getLore();

                    if (!lore.isEmpty() && lore.get(0).equalsIgnoreCase("QR Code Map")) {
                        player.getInventory().remove(itemStack);
                        player.updateInventory();
                    }
                }
            }
        }

        if (potPlayer == null) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            if (potPlayer.isFrozen())
                CorePlugin.getInstance().getPlayerManager().sendDisconnectFreezeMessage(event.getPlayer());

            if (potPlayer.isStaffMode()) {
                player.getInventory().clear();
                player.getInventory().setContents(potPlayer.getItemHistory());
                player.getInventory().setArmorContents(potPlayer.getArmorHistory());
            }

            CorePlugin.getInstance().getServerManager().getVanishedPlayers().remove(event.getPlayer());
            potPlayer.savePlayerData();
        });

        if (event.getPlayer().hasPermission("scandium.staff")) {
            new SwitchTask(event.getPlayer().getDisplayName()).runTaskLaterAsynchronously(CorePlugin.getInstance(), 60L);
        }
    }

    @RequiredArgsConstructor
    private final static class SwitchTask extends BukkitRunnable {

        private final String displayName;

        @Override
        public void run() {
            final NetworkServer server = CorePlugin.getInstance().getServerManager().getServer(ChatColor.stripColor(this.displayName));

            if (server != null) {
                if (!server.getServerName().equals(CorePlugin.getInstance().getServerName())) {
                    RedisUtil.publishAsync(RedisUtil.onSwitchServer(this.displayName, server.getServerName()));
                }
            } else {
                RedisUtil.publishAsync(RedisUtil.onDisconnect(this.displayName));
            }
        }
    }
}
