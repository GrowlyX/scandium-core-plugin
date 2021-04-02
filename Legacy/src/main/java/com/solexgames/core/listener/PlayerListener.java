package com.solexgames.core.listener;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.manager.ServerManager;
import com.solexgames.core.menu.IMenu;
import com.solexgames.core.menu.impl.grant.GrantSelectConfirmMenu;
import com.solexgames.core.menu.impl.grant.GrantSelectReasonMenu;
import com.solexgames.core.menu.impl.punish.PunishSelectDurationMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.media.MediaConstants;
import com.solexgames.core.player.punishment.PunishmentStrings;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.DateUtil;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.StringUtil;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;

/**
 * @author GrowlyX
 * @since 2021
 */

public class PlayerListener implements Listener {

    public ServerManager MANAGER = CorePlugin.getInstance().getServerManager();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!CorePlugin.CAN_JOIN) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, PunishmentStrings.SERVER_NOT_LOADED);
            return;
        }

        CorePlugin.getInstance().getPlayerManager().setupPlayer(event);

        event.allow();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPreLoginCheck(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getUniqueId());
            boolean isHub = CorePlugin.getInstance().getServerName().toLowerCase().contains("hub") || CorePlugin.getInstance().getServerName().toLowerCase().contains("lobby");

            if (potPlayer != null) {
                if (potPlayer.isCurrentlyRestricted() && !isHub) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, potPlayer.getRestrictionMessage());
                } else {
                    event.allow();
                }
            } else {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, PunishmentStrings.PLAYER_DATA_LOAD);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer());

        if (potPlayer == null) {
            event.getPlayer().kickPlayer(PunishmentStrings.PLAYER_DATA_LOAD);
            return;
        }

        potPlayer.onAfterDataLoad();

        CompletableFuture.runAsync(() -> {
            Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> CorePlugin.getInstance().getServerManager().getVanishedPlayers().stream()
                    .map(player -> CorePlugin.getInstance().getPlayerManager().getPlayer(player))
                    .filter(potPlayer1 -> potPlayer.getActiveGrant().getRank().getWeight() < potPlayer1.getActiveGrant().getRank().getWeight())
                    .forEach(player -> event.getPlayer().hidePlayer(player.getPlayer())), 35L);

            if (MANAGER.isClearChatJoin()) {
                for (int lines = 0; lines < 250; lines++) {
                    event.getPlayer().sendMessage("  ");
                }
            }

            if (MANAGER.isJoinMessageEnabled()) {
                if (MANAGER.isJoinMessageCentered()) {
                    MANAGER.getJoinMessage().forEach(s -> event.getPlayer().sendMessage(Color.translate(s.replace("%PLAYER%", event.getPlayer().getDisplayName()))));
                } else {
                    StringUtil.sendCenteredMessage(event.getPlayer(), (ArrayList<String>) MANAGER.getJoinMessage());
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
                event.getPlayer().sendMessage(ChatColor.GREEN + "You've been automatically disguised as " + potPlayer.getColorByRankColor() + potPlayer.getDisguiseRank().getName() + ChatColor.GREEN + "!");
            }
        });
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer());

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
            Player player = (Player) event.getEntity();
            PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

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
        Player player = event.getPlayer();
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
        String message = event.getMessage();

        boolean filtered = CorePlugin.getInstance().getFilterManager().isMessageFiltered(player, message);
        if (filtered) {
            if (player.hasPermission("scandium.filter.bypass")) {
                player.sendMessage(Color.translate("&cBe careful, that message would have been filtered!"));
            } else {
                event.setCancelled(true);
                player.sendMessage(Color.translate("&cThat message is not allowed!"));
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
        Matcher youtubeMatcher = MediaConstants.YOUTUBE_PROFILELINK_REGEX.matcher(event.getMessage());

        if (potPlayer.isGrantDurationEditing()) {
            event.setCancelled(true);

            if (event.getMessage().equalsIgnoreCase("cancel")) {
                player.sendMessage(Color.translate("&cCancelled the granting process."));

                potPlayer.setGrantDurationRank(null);
                potPlayer.setGrantDurationTarget(null);
                potPlayer.setGrantDurationEditing(false);
            } else if (message.equalsIgnoreCase("perm") || message.equalsIgnoreCase("permanent")) {
                new GrantSelectReasonMenu(player, potPlayer.getGrantDurationTarget(), -1L, potPlayer.getGrantDurationRank(), true, potPlayer.getGrantDurationScope()).open(player);

                potPlayer.setGrantDurationRank(null);
                potPlayer.setGrantDurationTarget(null);
                potPlayer.setGrantDurationEditing(false);
            } else {
                try {
                    new GrantSelectReasonMenu(player, potPlayer.getGrantDurationTarget(), System.currentTimeMillis() - DateUtil.parseDateDiff(event.getMessage(), false), potPlayer.getGrantDurationRank(), false, potPlayer.getGrantDurationScope()).open(player);

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

        if (potPlayer.isGrantEditing()) {
            event.setCancelled(true);

            if (event.getMessage().equalsIgnoreCase("cancel")) {
                player.sendMessage(Color.translate("&cCancelled the granting process."));
                potPlayer.setGrantTarget(null);
                potPlayer.setGrantRank(null);
                potPlayer.setGrantPerm(false);
            } else {
                player.sendMessage(Color.translate("&aSet the grant reason to &6'" + message + "'&a."));
                new GrantSelectConfirmMenu(potPlayer.getPlayer(), potPlayer.getGrantTarget(), potPlayer.getGrantRank(), potPlayer.getGrantDuration(), message, potPlayer.isGrantPerm(), potPlayer.getGrantScope()).open(player);
            }
            potPlayer.setGrantEditing(false);
            return;
        }

        if (potPlayer.isReasonEditing()) {
            event.setCancelled(true);

            if (event.getMessage().equalsIgnoreCase("cancel")) {
                player.sendMessage(Color.translate("&cCancelled the punishment process."));
                potPlayer.setReasonTarget(null);
                potPlayer.setReasonType(null);
            } else {
                player.sendMessage(Color.translate("&aSet the punishment reason to &6'" + message + "'&a."));
                new PunishSelectDurationMenu(potPlayer.getPlayer(), potPlayer.getReasonTarget(), message, potPlayer.getReasonType()).open(player);
            }
            potPlayer.setReasonEditing(false);
            return;
        }

        if (potPlayer.getMedia().getMediaData().isModifyingDiscordData()) {
            if (discordMatcher.matches()) {
                potPlayer.getMedia().setDiscord(event.getMessage());
                player.sendMessage(Color.translate("&aUpdated your discord to &e" + event.getMessage() + "&a!"));
                potPlayer.getMedia().getMediaData().setModifyingDiscordData(false);
            } else {
                player.sendMessage(Color.translate("&cThat's an invalid discord username!"));
                player.sendMessage(Color.translate("&cExample: Wumpus#1234"));
            }
            event.setCancelled(true);
            return;
        }

        if (CorePlugin.getInstance().getPlayerManager().getPlayer(player).getMedia().getMediaData().isModifyingInstaData()) {
            if (instaMatcher.matches()) {
                potPlayer.getMedia().setInstagram(event.getMessage());
                player.sendMessage(Color.translate("&aUpdated your instagram to &6" + event.getMessage() + "&a!"));
                potPlayer.getMedia().getMediaData().setModifyingInstaData(false);
            } else {
                player.sendMessage(Color.translate("&cThat's an invalid instagram username!"));
                player.sendMessage(Color.translate("&cExample: @SolexGames"));
            }
            event.setCancelled(true);
            return;
        }

        if (CorePlugin.getInstance().getPlayerManager().getPlayer(player).getMedia().getMediaData().isModifyingYoutubeData()) {
            if (youtubeMatcher.matches()) {
                potPlayer.getMedia().setYoutubeLink(event.getMessage());
                player.sendMessage(Color.translate("&aUpdated your youtube to &6" + event.getMessage() + "&a!"));
                potPlayer.getMedia().getMediaData().setModifyingYoutubeData(false);
            } else {
                player.sendMessage(Color.translate("&cThat's an invalid youtube link!"));
                player.sendMessage(Color.translate("&cExample: https://youtube.com/c/SolexGames/"));
            }
            event.setCancelled(true);
            return;
        }

        if (CorePlugin.getInstance().getPlayerManager().getPlayer(player).getMedia().getMediaData().isModifyingTwitterData()) {
            if (twitterMatcher.matches()) {
                potPlayer.getMedia().setTwitter(event.getMessage());
                player.sendMessage(Color.translate("&aUpdated your twitter to &6" + event.getMessage() + "&a!"));
                potPlayer.getMedia().getMediaData().setModifyingTwitterData(false);
            } else {
                player.sendMessage(Color.translate("&cThat's an invalid twitter link!"));
                player.sendMessage(Color.translate("&cExample: @SolexGames"));
            }
            event.setCancelled(true);
            return;
        }

        if (potPlayer.getChannel() != null) {
            event.setCancelled(true);
            RedisUtil.writeAsync(RedisUtil.onChatChannel(potPlayer.getChannel(), message, player));
            return;
        }

        if (potPlayer.isCurrentlyRestricted()) {
            player.sendMessage(Color.translate("&cYou cannot chat as you are currently restricted."));
            event.setCancelled(true);
            return;
        }

        if (player.hasMetadata("spectator")) {
            event.setCancelled(true);
            return;
        }

        if (CorePlugin.getInstance().getServerManager().isChatEnabled()) {
            if (!potPlayer.isCurrentlyMuted()) {
                if (!CorePlugin.CHAT_FORMAT_ENABLED) {
                    return;
                }

                checkChannel(event, player, potPlayer);
            } else {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Color.translate(PunishmentStrings.MUTE_MESSAGE));
            }
        } else {
            if (player.hasPermission("scandium.chat.bypass")) {
                if (!potPlayer.isCurrentlyMuted()) {
                    if (!CorePlugin.CHAT_FORMAT_ENABLED) {
                        return;
                    }

                    checkChannel(event, player, potPlayer);
                } else {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Color.translate(PunishmentStrings.MUTE_MESSAGE));
                }
            } else {
                event.setCancelled(true);
                switch (potPlayer.getLanguage()) {
                    case ENGLISH:
                        player.sendMessage(Color.translate("&cThe chat is currently muted. Please try chatting again later."));
                        break;
                    case FRENCH:
                        player.sendMessage(Color.translate("&cLe chat est actuellement désactivé. Veuillez réessayer plus tard."));
                        break;
                    case ITALIAN:
                        player.sendMessage(Color.translate("&cLa chat è attualmente disattivata. Prova a chattare di nuovo più tardi."));
                        break;
                    case GERMAN:
                        player.sendMessage(Color.translate("&cDer Chat ist derzeit stummgeschaltet. Bitte versuchen Sie es später noch einmal."));
                        break;
                    case SPANISH:
                        player.sendMessage(Color.translate("&cEl chat está silenciado actualmente. Intenta chatear de nuevo más tarde."));
                        break;
                }
            }
        }
    }

    private void checkChannel(AsyncPlayerChatEvent event, Player player, PotPlayer potPlayer) {
        if (event.getMessage().startsWith("!") && event.getPlayer().hasPermission(ChatChannelType.STAFF.getPermission())) {
            event.setCancelled(true);
            CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisManager().write(RedisUtil.onChatChannel(ChatChannelType.STAFF, event.getMessage().replace("!", ""), event.getPlayer())));
        } else if (event.getMessage().startsWith("#") && event.getPlayer().hasPermission(ChatChannelType.ADMIN.getPermission())) {
            event.setCancelled(true);
            CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisManager().write(RedisUtil.onChatChannel(ChatChannelType.ADMIN, event.getMessage().replace("#", ""), event.getPlayer())));
        } else if (event.getMessage().startsWith("$") && event.getPlayer().hasPermission(ChatChannelType.DEV.getPermission())) {
            event.setCancelled(true);
            CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisManager().write(RedisUtil.onChatChannel(ChatChannelType.DEV, event.getMessage().replace("$", ""), event.getPlayer())));
        } else if (event.getMessage().startsWith("@") && event.getPlayer().hasPermission(ChatChannelType.HOST.getPermission())) {
            event.setCancelled(true);
            CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisManager().write(RedisUtil.onChatChannel(ChatChannelType.HOST, event.getMessage().replace("@", ""), event.getPlayer())));
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
        if (potPlayer.isFrozen()) {
            event.setCancelled(true);
            return;
        }

        if (potPlayer.isCurrentlyRestricted() && !event.getMessage().startsWith("/discord")) {
            event.getPlayer().sendMessage(Color.translate("&cYou cannot execute commands as you are currently banned."));
            event.getPlayer().sendMessage(Color.translate("&cThe only command you can execute is &4/discord&c."));

            event.setCancelled(true);
            return;
        }

        if (event.getMessage().contains(":") && !event.getPlayer().isOp()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Color.translate("&cThat syntax is not accepted."));
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

        if (CorePlugin.ANTI_CMD_SPAM)
            CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer()).setCommandCooldown(System.currentTimeMillis() + 1L);
        else CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer()).setCommandCooldown(0L);
    }

    private void checkThenSend(AsyncPlayerChatEvent event, Player player, PotPlayer potPlayer, long slowChat) {
        if (event.isCancelled()) {
            return;
        }

        Bukkit.getOnlinePlayers().stream()
                .map(player1 -> CorePlugin.getInstance().getPlayerManager().getPlayer(player1))
                .filter(Objects::nonNull)
                .filter(potPlayer1 -> potPlayer1.isIgnoring(potPlayer.getPlayer()) && potPlayer.isCanSeeGlobalChat())
                .forEach(potPlayer1 -> potPlayer1.getPlayer().sendMessage(Color.translate(CorePlugin.CHAT_FORMAT
                        .replace("<prefix>", (potPlayer.getAppliedPrefix() != null ? potPlayer.getAppliedPrefix().getPrefix() + " " : ""))
                        .replace("<rank_prefix>", (potPlayer.getDisguiseRank() != null ? potPlayer.getDisguiseRank().getPrefix() : potPlayer.getActiveGrant().getRank().getPrefix()))
                        .replace("<rank_suffix>", (potPlayer.getDisguiseRank() != null ? potPlayer.getDisguiseRank().getSuffix() : potPlayer.getActiveGrant().getRank().getSuffix()))
                        .replace("<rank_color>", (potPlayer.getDisguiseRank() != null ? potPlayer.getDisguiseRank().getColor() : potPlayer.getActiveGrant().getRank().getColor()))
                        .replace("<custom_color>", (potPlayer.getCustomColor() != null ? potPlayer.getCustomColor().toString() : ""))
                        .replace("<player_name>", player.getName()))
                        .replace("<message>", event.getMessage())
                ));

        if (CorePlugin.ANTI_CHAT_SPAM)
            CorePlugin.getInstance().getPlayerManager().getPlayer(player).setChatCooldown(System.currentTimeMillis() + (slowChat > 0L ? slowChat : 3000L));
        else CorePlugin.getInstance().getPlayerManager().getPlayer(player).setChatCooldown(0L);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        Player player = event.getPlayer();

        if (potPlayer == null) {
            return;
        }

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
                if (CorePlugin.getInstance().getServerManager().getServer(event.getPlayer().getName()) != null) {
                    RedisUtil.writeAsync(RedisUtil.onSwitchServer(event.getPlayer().getDisplayName(), CorePlugin.getInstance().getServerManager().getServer(event.getPlayer().getName()).getServerName()));
                } else {
                    RedisUtil.writeAsync(RedisUtil.onDisconnect(event.getPlayer().getDisplayName()));
                }
            }, 49L);
        }
    }
}
