package com.solexgames.core.listener;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.chat.IChatCheck;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.enums.ServerType;
import com.solexgames.core.manager.ServerManager;
import com.solexgames.core.menu.AbstractInventoryMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.punishment.PunishmentStrings;
import com.solexgames.core.server.NetworkServer;
import com.solexgames.core.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
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
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

/**
 * @author GrowlyX
 * @since 2021
 */

public class PlayerListener implements Listener {

    public ServerManager serverManager = CorePlugin.getInstance().getServerManager();

    public static final long HOUR = 3600 * 1000;

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
                if (potPlayer.isCurrentlyBlacklisted()) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, potPlayer.getRestrictionMessage());
                    return;
                }

                if (potPlayer.isCurrentlyRestricted() && !isHub) {
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
                    this.serverManager.getJoinMessage().forEach(s -> event.getPlayer().sendMessage(s.replace("%PLAYER%", event.getPlayer().getDisplayName())));
                } else {
                    StringUtil.sendCenteredMessage(event.getPlayer(), (ArrayList<String>) this.serverManager.getJoinMessage());
                }
            }

            if (event.getPlayer().hasPermission("scandium.staff") && CorePlugin.getInstance().getServerManager().isJoinStaffEnabled()) {
                CorePlugin.getInstance().getServerManager().getStaffInformation().forEach(s -> event.getPlayer().sendMessage(s
                        .replace("<nice_char>", Character.toString('»'))
                        .replace("<channel>", ChatColor.RED + "None")
                        .replace("<messages>", (potPlayer.isCanSeeStaffMessages() ? ChatColor.GREEN + "Shown" : ChatColor.RED + "Hidden"))
                        .replace("<filter>", (potPlayer.isCanSeeFiltered() ? ChatColor.GREEN + "Shown" : ChatColor.RED + "Hidden"))
                        .replace("<modmode>", (potPlayer.isStaffMode() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"))
                        .replace("<vanish>", (potPlayer.isVanished() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"))
                ));
            }

            if (event.getPlayer().hasPermission("scandium.2fa") && CorePlugin.getInstance().getServerSettings().isTwoFactorEnabled()) {
                if (potPlayer.isAuthBypassed()) {
                    event.getPlayer().sendMessage(Constants.STAFF_PREFIX + Color.SECONDARY_COLOR + "You've been automatically authenticated.");
                } else if (potPlayer.isHasSetup2FA()) {
                    if (!potPlayer.getPreviousIpAddress().equals(potPlayer.getIpAddress()) || potPlayer.isRequiredToAuth() || (potPlayer.getLastAuth() + (HOUR * 6L)) < System.currentTimeMillis()) {
                        final String message = Constants.STAFF_PREFIX + Color.SECONDARY_COLOR + "Please authenticate via " + Color.MAIN_COLOR + "/auth " + ChatColor.WHITE + "<code>" + Color.SECONDARY_COLOR + ".";

                        event.getPlayer().sendMessage(message);
                        LockedState.lock(event.getPlayer(), message);

                        potPlayer.setRequiredToAuth(true);
                    } else {
                        event.getPlayer().sendMessage(Constants.STAFF_PREFIX + Color.SECONDARY_COLOR + "You've been automatically authenticated.");
                    }
                } else {
                    final String message = Constants.STAFF_PREFIX + Color.SECONDARY_COLOR + "Please setup two-factor authentication via " + Color.MAIN_COLOR + "/authsetup" + Color.SECONDARY_COLOR + ".";

                    event.getPlayer().sendMessage(message);
                    LockedState.lock(event.getPlayer(), message);
                }
            }

            if (potPlayer.isAutoModMode() && !CorePlugin.getInstance().getServerName().contains("hub")) {
                potPlayer.getPlayer().sendMessage(Color.translate(CorePlugin.getInstance().getServerManager().getAutomaticallyPutInto().replace("<value>", "mod mode")));

                CorePlugin.getInstance().getPlayerManager().modModeRaw(potPlayer.getPlayer());
            }

            if (potPlayer.isCurrentlyRestricted() || potPlayer.isCurrentlyBlacklisted()) {
                event.getPlayer().sendMessage(potPlayer.getRestrictionMessage());
            }

            if (potPlayer.isHasActiveWarning()) {
                event.getPlayer().sendMessage(potPlayer.getWarningMessage());
            }

            if (potPlayer.getDisguiseRank() != null) {
                event.getPlayer().sendMessage(Color.SECONDARY_COLOR + "You've been automatically disguised as " + potPlayer.getColorByRankColorWithItalic() + potPlayer.getDisguiseRank().getName() + Color.SECONDARY_COLOR + "!");
            }
        });

         CorePlugin.getInstance().getNMS().setupTablist(event.getPlayer());

        if (potPlayer.isAutoVanish()) {
            event.getPlayer().sendMessage(CorePlugin.getInstance().getServerManager().getAutomaticallyPutInto().replace("<value>", "vanish"));

            Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> CorePlugin.getInstance().getPlayerManager().vanishPlayerRaw(event.getPlayer(), 0), 7L);
        }
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
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof AbstractInventoryMenu) {
            ((AbstractInventoryMenu) event.getInventory().getHolder()).onInventoryClick(event);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof AbstractInventoryMenu) {
            ((AbstractInventoryMenu) event.getInventory().getHolder()).onInventoryDrag(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof AbstractInventoryMenu) {
            ((AbstractInventoryMenu) event.getInventory().getHolder()).onInventoryClose(event);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        final String message = event.getMessage();

        if (potPlayer == null) {
            player.sendMessage(ChatColor.RED + "Your account is temporarily blocked from performing actions.");
            player.sendMessage(ChatColor.RED + "Please reconnect to resolve this issue.");

            event.setCancelled(true);
            return;
        }

        if (potPlayer.isCurrentlyRestricted()) {
            player.sendMessage(ChatColor.RED + "You cannot chat as you're currently restricted.");

            event.setCancelled(true);
            return;
        }

        if (LockedState.isLocked(event.getPlayer())) {
            player.sendMessage(ChatColor.RED + "You cannot chat as you need to authenticate.");

            event.setCancelled(true);
            return;
        }

        final boolean filtered = CorePlugin.getInstance().getFilterManager().isMessageFiltered(player, message);

        if (filtered && !player.hasPermission("scandium.filter.bypass")) {
            player.sendMessage(ChatColor.RED + "That message has been filtered as it has a blocked term in it.");
            CorePlugin.getInstance().getFilterManager().handleAlert(player, message);

            event.setCancelled(true);

            return;
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

        for (IChatCheck chatCheck : CorePlugin.getInstance().getChatCheckList()) {
            if (!event.isCancelled()) {
                chatCheck.check(event, potPlayer);
            }
        }

        if (event.isCancelled()) {
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

            if (System.currentTimeMillis() < potPlayer.getChatCooldown()) {
                if (player.hasPermission("scandium.chat.cooldown.bypass")) {
                    this.checkThenSend(event, player, potPlayer, slowChat);
                } else {
                    player.sendMessage(slowChat > 0L ?
                            PunishmentStrings.SLOW_CHAT_MESSAGE.replace("<amount>", DurationFormatUtils.formatDurationWords(potPlayer.getChatCooldown() - System.currentTimeMillis(), true, true)) :
                            PunishmentStrings.COOL_DOWN_MESSAGE.replace("<amount>", DurationFormatUtils.formatDurationWords(potPlayer.getChatCooldown() - System.currentTimeMillis(), true, true))
                    );

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
        final String lowercaseCommand = event.getMessage().toLowerCase();

        if (potPlayer == null) {
            player.sendMessage(ChatColor.RED + "Your account is temporarily blocked from performing actions.");
            player.sendMessage(ChatColor.RED + "Please reconnect to resolve this issue.");

            event.setCancelled(true);
            return;
        }

        if (LockedState.isLocked(event.getPlayer()) && !(lowercaseCommand.startsWith("/2fa") || lowercaseCommand.startsWith("/auth") || lowercaseCommand.startsWith("/authsetup"))) {
            player.sendMessage(ChatColor.RED + "You cannot issue commands as you haven't authenticated.");
            player.sendMessage(ChatColor.RED + "The only command you can perform is " + ChatColor.YELLOW + "/2fa or /authsetup" + ChatColor.RED + "!");

            event.setCancelled(true);
            return;
        }

        if (potPlayer.isFrozen()) {
            event.setCancelled(true);
            return;
        }

        if (potPlayer.isCurrentlyRestricted() && !lowercaseCommand.startsWith("/register")) {
            player.sendMessage(ChatColor.RED + "You cannot issue commands as you are banned.");
            player.sendMessage(ChatColor.RED + "The only command you can perform is " + ChatColor.YELLOW + "/register" + ChatColor.RED + "!");

            event.setCancelled(true);
            return;
        }

        if (lowercaseCommand.split(" ")[0].contains(":") && !event.getPlayer().isOp()) {
            player.sendMessage(ChatColor.RED + "You cannot execute commands with semi-colons.");

            event.setCancelled(true);
            return;
        }

        if (!player.hasPermission("scandium.command.block.bypass")) {
            for (String command : CorePlugin.getInstance().getServerManager().getBlockedCommands()) {
                if (lowercaseCommand.startsWith("/" + command) && (lowercaseCommand.equals("/" + command) || lowercaseCommand.startsWith("/" + command + " "))) {
                    player.sendMessage(CorePlugin.getInstance().getServerManager().getCommandCallback());
                    event.setCancelled(true);

                    return;
                }
            }
        }

        final long commandCoolDown = 20L;

        if (System.currentTimeMillis() < potPlayer.getCommandCooldown()) {
            if (!player.hasPermission("scandium.command.cooldown.bypass")) {
                player.sendMessage(Color.translate(PunishmentStrings.CMD_CHAT_MESSAGE.replace("<amount>", DurationFormatUtils.formatDurationWords(potPlayer.getCommandCooldown() - System.currentTimeMillis(), true, true))));
                event.setCancelled(true);
            }
        }

        if (CorePlugin.getInstance().getServerSettings().isAntiCommandSpamEnabled()) {
            potPlayer.setCommandCooldown(System.currentTimeMillis() + commandCoolDown);
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
            potPlayer.setChatCooldown(System.currentTimeMillis() + (slowChat > 0L ? slowChat : 2000L));
        } else {
            potPlayer.setChatCooldown(0L);
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        final Player player = event.getPlayer();

        if (LockedState.isLocked(player)) {
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null && itemStack.getType() == Material.MAP && itemStack.getItemMeta().hasLore()) {
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
            if (!CorePlugin.getInstance().getServerSettings().isUsingXenon()) {
                new SwitchTask(event.getPlayer().getDisplayName()).runTaskLaterAsynchronously(CorePlugin.getInstance(), 40L);
            }
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
                } else {
                    new SwitchTask(this.displayName).runTaskLaterAsynchronously(CorePlugin.getInstance(), 20L);
                }
            } else {
                RedisUtil.publishAsync(RedisUtil.onDisconnect(this.displayName));
            }
        }
    }
}
