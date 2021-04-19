package com.solexgames.core.listener;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.manager.ServerManager;
import com.solexgames.core.menu.IMenu;
import com.solexgames.core.menu.impl.grant.GrantSelectConfirmMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.media.MediaConstants;
import com.solexgames.core.player.punishment.PunishmentStrings;
import com.solexgames.core.server.NetworkServer;
import com.solexgames.core.util.*;
import com.solexgames.core.util.external.pagination.impl.GrantReasonPaginatedMenu;
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
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;

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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPreLoginCheck(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getUniqueId());
            final boolean isHub = CorePlugin.getInstance().getServerName().toLowerCase().contains("hub") || CorePlugin.getInstance().getServerName().toLowerCase().contains("lobby");

            if (potPlayer != null) {
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

        if (potPlayer != null) {
            if (potPlayer.isFrozen()) {
                event.setCancelled(true);
                event.getPlayer().teleport(event.getFrom());
            }
        }
    }

    @EventHandler
    public void onDamaged(EntityDamageEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER)) {
            final Player player = (Player) event.getEntity();
            final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

            if (potPlayer != null) {
                if (potPlayer.isFrozen()) {
                    event.setCancelled(true);
                }
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

        if (LockedState.isLocked(player)) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot perform this action right now!");
            event.getPlayer().sendMessage(ChatColor.RED + "The only action you can perform is " + ChatColor.DARK_RED + "/2fa" + ChatColor.RED + "!");

            event.setCancelled(true);
        }

        boolean filtered = CorePlugin.getInstance().getFilterManager().isMessageFiltered(player, message);
        if (filtered) {
            if (player.hasPermission("scandium.filter.bypass")) {
                player.sendMessage(ChatColor.RED + "Be careful, that message would have been filtered!");
            } else {
                event.setCancelled(true);

                player.sendMessage(ChatColor.RED + "Error: That message has been filtered as it has a blocked term in it.");
                return;
            }
        }

        if (potPlayer.isFrozen()) {
            event.setCancelled(true);

            Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(player1 -> player1.hasPermission("scandium.staff"))
                    .filter(player1 -> CorePlugin.getInstance().getPlayerManager().getPlayer(player1).isCanSeeStaffMessages())
                    .forEach(player1 -> player1.sendMessage(Color.translate("&c[Frozen] &f" + potPlayer.getPlayer().getDisplayName() + "&7: &e" + event.getMessage())));

            event.getPlayer().sendMessage(Color.translate(Color.translate("&c[Frozen] &f" + potPlayer.getPlayer().getDisplayName() + "&7: &e" + event.getMessage())));
            return;
        }

        Matcher discordMatcher = MediaConstants.DISCORD_USERNAME_REGEX.matcher(event.getMessage());
        Matcher twitterMatcher = MediaConstants.TWITTER_USERNAME_REGEX.matcher(event.getMessage());
        Matcher instaMatcher = MediaConstants.INSTAGRAM_USERNAME_REGEX.matcher(event.getMessage());
        Matcher youtubeMatcher = MediaConstants.YOUTUBE_PROFILE_LINK_REGEX.matcher(event.getMessage());

        if (potPlayer.isGrantDurationEditing()) {
            event.setCancelled(true);

            if (event.getMessage().equalsIgnoreCase("cancel")) {
                player.sendMessage(ChatColor.RED + ("Cancelled the granting process."));

                potPlayer.setGrantDurationRank(null);
                potPlayer.setGrantDurationTarget(null);
                potPlayer.setGrantDurationEditing(false);
            } else if (message.equalsIgnoreCase("perm") || message.equalsIgnoreCase("permanent")) {
                new GrantReasonPaginatedMenu(player, potPlayer.getGrantDurationTarget(), -1L, potPlayer.getGrantDurationRank(), true, potPlayer.getGrantDurationScope()).openMenu(player);

                potPlayer.setGrantDurationRank(null);
                potPlayer.setGrantDurationTarget(null);
                potPlayer.setGrantDurationEditing(false);
            } else {
                try {
                    new GrantReasonPaginatedMenu(player, potPlayer.getGrantDurationTarget(), System.currentTimeMillis() - DateUtil.parseDateDiff(event.getMessage(), false), potPlayer.getGrantDurationRank(), false, potPlayer.getGrantDurationScope()).openMenu(player);

                    potPlayer.setGrantDurationRank(null);
                    potPlayer.setGrantDurationTarget(null);
                    potPlayer.setGrantDurationEditing(false);
                } catch (Exception ignored) {
                    player.sendMessage(ChatColor.RED + "Invalid duration.");
                    return;
                }
            }
            return;
        }

        // TODO: Switch these to Prompts

        if (potPlayer.isGrantEditing()) {
            event.setCancelled(true);

            if (event.getMessage().equalsIgnoreCase("cancel")) {
                player.sendMessage(ChatColor.RED + ("Cancelled the granting process."));
                potPlayer.setGrantTarget(null);
                potPlayer.setGrantRank(null);
                potPlayer.setGrantPerm(false);
            } else {
                player.sendMessage(ChatColor.GREEN + Color.translate("Set the grant reason to &6'" + message + "'&a."));
                new GrantSelectConfirmMenu(potPlayer.getPlayer(), potPlayer.getGrantTarget(), potPlayer.getGrantRank(), potPlayer.getGrantDuration(), message, potPlayer.isGrantPerm(), potPlayer.getGrantScope()).open(player);
            }

            potPlayer.setGrantEditing(false);
            return;
        }

        if (potPlayer.getMedia().getMediaData().isModifyingDiscord()) {
            if (discordMatcher.matches()) {
                potPlayer.getMedia().setDiscord(event.getMessage());
                player.sendMessage(ChatColor.GREEN + Color.translate("Updated your discord to &e" + event.getMessage() + ChatColor.GREEN + "!"));
                potPlayer.getMedia().getMediaData().setModifyingDiscord(false);
            } else {
                player.sendMessage(ChatColor.RED + ("Error: That's an invalid discord username!"));
                player.sendMessage(ChatColor.RED + ("Example: Wumpus#1234"));
            }
            event.setCancelled(true);
            return;
        }

        if (CorePlugin.getInstance().getPlayerManager().getPlayer(player).getMedia().getMediaData().isModifyingInsta()) {
            if (instaMatcher.matches()) {
                potPlayer.getMedia().setInstagram(event.getMessage());
                player.sendMessage(ChatColor.GREEN + Color.translate("Updated your instagram to &6" + event.getMessage() + ChatColor.GREEN + "!"));
                potPlayer.getMedia().getMediaData().setModifyingInsta(false);
            } else {
                player.sendMessage(ChatColor.RED + ("Error: That's an invalid instagram username!"));
                player.sendMessage(ChatColor.RED + ("Example: @SolexGames"));
            }
            event.setCancelled(true);
            return;
        }

        if (CorePlugin.getInstance().getPlayerManager().getPlayer(player).getMedia().getMediaData().isModifyingYouTube()) {
            if (youtubeMatcher.matches()) {
                potPlayer.getMedia().setYoutubeLink(event.getMessage());
                player.sendMessage(ChatColor.GREEN + Color.translate("Updated your youtube to &6" + event.getMessage() + ChatColor.GREEN + "!"));
                potPlayer.getMedia().getMediaData().setModifyingYouTube(false);
            } else {
                player.sendMessage(ChatColor.RED + ("Error: That's an invalid youtube link!"));
                player.sendMessage(ChatColor.RED + ("Example: https://youtube.com/c/SolexGames/"));
            }
            event.setCancelled(true);
            return;
        }

        if (CorePlugin.getInstance().getPlayerManager().getPlayer(player).getMedia().getMediaData().isModifyingTwitter()) {
            if (twitterMatcher.matches()) {
                potPlayer.getMedia().setTwitter(event.getMessage());
                player.sendMessage(ChatColor.GREEN + Color.translate("Updated your twitter to &6" + event.getMessage() + ChatColor.GREEN + "!"));
                potPlayer.getMedia().getMediaData().setModifyingTwitter(false);
            } else {
                player.sendMessage(ChatColor.RED + ("Error: That's an invalid twitter link!"));
                player.sendMessage(ChatColor.RED + ("Example: @SolexGames"));
            }
            event.setCancelled(true);
            return;
        }

        if (potPlayer.getChannel() != null) {
            RedisUtil.writeAsync(RedisUtil.onChatChannel(potPlayer.getChannel(), message, player));

            event.setCancelled(true);
            return;
        }

        if (potPlayer.isCurrentlyRestricted()) {
            player.sendMessage(ChatColor.RED + "You cannot chat as you are currently restricted.");

            event.setCancelled(true);
            return;
        }

        if (player.hasMetadata("spectator")) {
            event.setCancelled(true);
            return;
        }

        if (CorePlugin.getInstance().getServerManager().isChatEnabled()) {
            if (!potPlayer.isCurrentlyMuted()) {
                if (!CorePlugin.getInstance().getServerSettings().isChatFormatEnabled()) {
                    return;
                }

                this.checkChannel(event, player, potPlayer);
            } else {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Color.translate(PunishmentStrings.MUTE_MESSAGE));
            }
        } else {
            if (player.hasPermission("scandium.chat.bypass")) {
                if (!potPlayer.isCurrentlyMuted()) {
                    if (!CorePlugin.getInstance().getServerSettings().isChatFormatEnabled()) {
                        return;
                    }

                    this.checkChannel(event, player, potPlayer);
                } else {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Color.translate(PunishmentStrings.MUTE_MESSAGE));
                }
            } else {
                event.setCancelled(true);
                switch (potPlayer.getLanguage()) {
                    case ENGLISH:
                        player.sendMessage(ChatColor.RED + "The chat is currently muted. Please try chatting again later.");
                        break;
                    case FRENCH:
                        player.sendMessage(ChatColor.RED + "Le chat est actuellement désactivé. Veuillez réessayer plus tard.");
                        break;
                    case ITALIAN:
                        player.sendMessage(ChatColor.RED + "La chat è attualmente disattivata. Prova a chattare di nuovo più tardi.");
                        break;
                    case GERMAN:
                        player.sendMessage(ChatColor.RED + "Der Chat ist derzeit stummgeschaltet. Bitte versuchen Sie es später noch einmal.");
                        break;
                    case SPANISH:
                        player.sendMessage(ChatColor.RED + "El chat está silenciado actualmente. Intenta chatear de nuevo más tarde.");
                        break;
                }
            }
        }
    }

    private void checkChannel(AsyncPlayerChatEvent event, Player player, PotPlayer potPlayer) {
        if (event.getMessage().startsWith("! ") && event.getPlayer().hasPermission(ChatChannelType.STAFF.getPermission())) {
            event.setCancelled(true);

            RedisUtil.writeAsync(RedisUtil.onChatChannel(ChatChannelType.STAFF, event.getMessage().replace("! ", ""), event.getPlayer()));
        } else if (event.getMessage().startsWith("# ") && event.getPlayer().hasPermission(ChatChannelType.ADMIN.getPermission())) {
            event.setCancelled(true);

            RedisUtil.writeAsync(RedisUtil.onChatChannel(ChatChannelType.ADMIN, event.getMessage().replace("# ", ""), event.getPlayer()));
        } else if (event.getMessage().startsWith("$ ") && event.getPlayer().hasPermission(ChatChannelType.DEV.getPermission())) {
            event.setCancelled(true);

            RedisUtil.writeAsync(RedisUtil.onChatChannel(ChatChannelType.DEV, event.getMessage().replace("$ ", ""), event.getPlayer()));
        } else if (event.getMessage().startsWith("@ ") && event.getPlayer().hasPermission(ChatChannelType.HOST.getPermission())) {
            event.setCancelled(true);

            RedisUtil.writeAsync(RedisUtil.onChatChannel(ChatChannelType.HOST, event.getMessage().replace("@ ", ""), event.getPlayer()));
        } else {
            long slowChat = CorePlugin.getInstance().getServerManager().getChatSlow();

            if ((System.currentTimeMillis() < CorePlugin.getInstance().getPlayerManager().getPlayer(player).getChatCooldown())) {
                if (player.hasPermission("scandium.chat.cooldown.bypass")) {
                    checkThenSend(event, player, potPlayer, slowChat);
                } else {
                    player.sendMessage(slowChat > 0L ? Color.translate(PunishmentStrings.SLOW_CHAT_MESSAGE.replace("<amount>", DurationFormatUtils.formatDurationWords(slowChat, true, true))) : Color.translate(PunishmentStrings.COOL_DOWN_MESSAGE));
                    event.setCancelled(true);
                }
            } else {
                checkThenSend(event, player, potPlayer, slowChat);
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer());

        if (potPlayer == null) {
            event.setCancelled(true);
            return;
        }

        if (LockedState.isLocked(event.getPlayer())) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot perform this action right now!");
            event.getPlayer().sendMessage(ChatColor.RED + "The only action you can perform is " + ChatColor.RED + ChatColor.BOLD.toString() + "/2fa" + ChatColor.RED + "!");

            event.setCancelled(true);
        }

        if (potPlayer.isFrozen()) {
            event.setCancelled(true);
            return;
        }

        if (potPlayer.isCurrentlyRestricted() && !event.getMessage().startsWith("/discord")) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot perform this command as you are currently banned.");
            event.getPlayer().sendMessage(ChatColor.RED + "The only command you can perform is " + ChatColor.RED + ChatColor.BOLD.toString() + "/discord" + ChatColor.RED + "!");

            event.setCancelled(true);
            return;
        }

        if (event.getMessage().contains(":") && !event.getPlayer().isOp()) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot execute commands with semi-colons.");

            event.setCancelled(true);
        }

        if (CorePlugin.getInstance().getConfig().getBoolean("block-commands.enabled")) {
            if (event.getPlayer().hasPermission("scandium.protocol.bypass")) return;
            CorePlugin.getInstance().getConfig().getStringList("block-commands.list").forEach(s -> {
                if (event.getMessage().startsWith("/" + s) && (event.getMessage().length() <= ("/" + s).length())) {
                    event.getPlayer().sendMessage(Color.translate(CorePlugin.getInstance().getConfig().getString("block-commands.message")));
                    event.setCancelled(true);
                }

                if (event.getMessage().startsWith("/" + s) && event.getMessage().contains(" ")) {
                    event.getPlayer().sendMessage(Color.translate(CorePlugin.getInstance().getConfig().getString("block-commands.message")));
                    event.setCancelled(true);
                }
            });
        }

        long commandCoolDown = 1L;
        if (System.currentTimeMillis() < potPlayer.getCommandCooldown()) {
            if (!event.getPlayer().hasPermission("scandium.command.cooldown.bypass")) {
                event.getPlayer().sendMessage(Color.translate(PunishmentStrings.CMD_CHAT_MESSAGE.replace("<amount>", DurationFormatUtils.formatDurationWords(commandCoolDown, true, true))));
                event.setCancelled(true);
            }
        }

        if (CorePlugin.getInstance().getServerSettings().isAntiCommandSpamEnabled())
            CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer()).setCommandCooldown(System.currentTimeMillis() + 1L);
        else CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer()).setCommandCooldown(0L);
    }

    private void checkThenSend(AsyncPlayerChatEvent event, Player player, PotPlayer potPlayer, long slowChat) {
        if (event.isCancelled()) {
            return;
        }

        Bukkit.getOnlinePlayers().stream()
                .map(player1 -> CorePlugin.getInstance().getPlayerManager().getPlayer(player1))
                .filter(potPlayer1 -> potPlayer1 != null && potPlayer1.isIgnoring(potPlayer.getPlayer()) && potPlayer.isCanSeeGlobalChat())
                .forEach(potPlayer1 -> potPlayer1.getPlayer().sendMessage(Color.translate(CorePlugin.getInstance().getServerSettings().getChatFormat()
                        .replace("<prefix>", (potPlayer.getAppliedPrefix() != null ? potPlayer.getAppliedPrefix().getPrefix() + " " : ""))
                        .replace("<rank_prefix>", (potPlayer.getDisguiseRank() != null ? potPlayer.getDisguiseRank().getPrefix() : potPlayer.getActiveGrant().getRank().getPrefix()))
                        .replace("<rank_suffix>", (potPlayer.getDisguiseRank() != null ? potPlayer.getDisguiseRank().getSuffix() : potPlayer.getActiveGrant().getRank().getSuffix()))
                        .replace("<rank_color>", (potPlayer.getDisguiseRank() != null ? potPlayer.getDisguiseRank().getColor() : potPlayer.getActiveGrant().getRank().getColor()))
                        .replace("<custom_color>", (potPlayer.getCustomColor() != null ? potPlayer.getCustomColor().toString() : ""))
                        .replace("<player_name>", player.getName()))
                        .replace("<message>", event.getMessage())
                ));

        if (CorePlugin.getInstance().getServerSettings().isAntiSpamEnabled())
            CorePlugin.getInstance().getPlayerManager().getPlayer(player).setChatCooldown(System.currentTimeMillis() + (slowChat > 0L ? slowChat : 3000L));
        else CorePlugin.getInstance().getPlayerManager().getPlayer(player).setChatCooldown(0L);
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

            if (event.getPlayer().hasPermission("scandium.staff")) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(CorePlugin.getInstance(), () -> {
                    NetworkServer server = CorePlugin.getInstance().getServerManager().getServer(event.getPlayer().getName());

                    if (server != null) {
                        RedisUtil.writeAsync(RedisUtil.onSwitchServer(event.getPlayer().getDisplayName(), server.getServerName()));
                    } else {
                        RedisUtil.writeAsync(RedisUtil.onDisconnect(event.getPlayer()));
                    }
                }, 80L);
            }
        });
    }
}