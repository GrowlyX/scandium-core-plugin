package com.solexgames.core.listener;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.manager.ServerManager;
import com.solexgames.core.media.MediaConstants;
import com.solexgames.core.menu.IMenu;
import com.solexgames.core.menu.extend.grant.GrantSelectConfirmMenu;
import com.solexgames.core.menu.extend.grant.GrantSelectReasonMenu;
import com.solexgames.core.menu.extend.punish.PunishSelectDurationMenu;
import com.solexgames.core.player.PotPlayer;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentStrings;
import com.solexgames.core.util.*;
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
import org.bukkit.event.server.ServerListPingEvent;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;

public class PlayerListener implements Listener {

    public ServerManager MANAGER = CorePlugin.getInstance().getServerManager();

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        if (CorePlugin.getInstance().getWhitelistConfig().getBoolean("whitelist")) {
            event.setMotd(MOTDUtil.getWhitelistedMotd());
        } else {
            event.setMotd(MOTDUtil.getNormalMotd());
        }
    }

    @EventHandler
    public void onConnect(AsyncPlayerPreLoginEvent event) {
        if (CorePlugin.CAN_JOIN) {
            if (CorePlugin.getInstance().getWhitelistConfig().getConfiguration().getBoolean("beta-whitelist")) {
                if (CorePlugin.getInstance().getServerManager().getBetaWhitelistedPlayers().contains(event.getName())) {
                    if (CorePlugin.getInstance().getWhitelistConfig().getBoolean("beta-whitelist-can-join")) {
                        allowConnection(event);
                    } else {
                        if (CorePlugin.getInstance().getWhitelistConfig().getConfiguration().getBoolean("whitelist")) {
                            if (!CorePlugin.getInstance().getServerManager().getWhitelistedPlayers().contains(event.getName())) {
                                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Color.translate(CorePlugin.getInstance().getWhitelistConfig().getConfiguration().getString("beta-whitelisted-msg").replace("<nl>", "\n")));
                            } else {
                                allowConnection(event);
                            }
                        } else {
                            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Color.translate(CorePlugin.getInstance().getWhitelistConfig().getConfiguration().getString("beta-whitelisted-msg").replace("<nl>", "\n")));
                        }
                    }
                } else {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Color.translate(CorePlugin.getInstance().getWhitelistConfig().getConfiguration().getString("beta-whitelisted-msg").replace("<nl>", "\n")));
                }
            }
            if (CorePlugin.getInstance().getWhitelistConfig().getConfiguration().getBoolean("whitelist")) {
                if (!CorePlugin.getInstance().getServerManager().getWhitelistedPlayers().contains(event.getName())) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Color.translate(CorePlugin.getInstance().getWhitelistConfig().getConfiguration().getString("whitelisted-msg").replace("<nl>", "\n")));
                } else {
                    allowConnection(event);
                }
            } else {
                allowConnection(event);
            }
        } else {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Color.translate("&cThe server is currently booting...\n&cPlease reconnect in a few minutes."));
        }
    }

    private void allowConnection(AsyncPlayerPreLoginEvent event) {
        Punishment.getAllPunishments()
                .stream()
                .filter(punishment -> punishment.getTarget().equals(event.getUniqueId()))
                .filter(Punishment::isActive)
                .filter(punishment -> !punishment.isRemoved())
                .forEach(punishment -> {
                    switch (punishment.getPunishmentType()) {
                        case BLACKLIST:
                            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Color.translate(PunishmentStrings.BLACK_LIST_MESSAGE.replace("<reason>", punishment.getReason())));
                            break;
                        case IPBAN:
                        case BAN:
                            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, (punishment.isPermanent() ? Color.translate(PunishmentStrings.BAN_MESSAGE_PERM.replace("<reason>", punishment.getReason())) : Color.translate(PunishmentStrings.BAN_MESSAGE_TEMP.replace("<reason>", punishment.getReason()).replace("<time>", punishment.getDurationString()))));
                            break;
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

    @EventHandler
    public void onConnect(PlayerJoinEvent event) {
        PotPlayer potPlayer = new PotPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getAddress().getAddress());
        ServerManager serverManager = CorePlugin.getInstance().getServerManager();

        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
            CorePlugin.getInstance().getServerManager().getVanishedPlayers().forEach(player -> {
                if (!event.getPlayer().hasPermission("scandium.vanished.see")) {
                    event.getPlayer().hidePlayer(player);
                }
            });

            if (MANAGER.isClearChatJoin()) {
                Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
                    for (int lines = 0; lines < 250; lines++) {
                        event.getPlayer().sendMessage("  ");
                    }
                });
            }

            if (MANAGER.isJoinMessageEnabled()) {
                if (MANAGER.isJoinMessageCentered()) {
                    Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> MANAGER.getJoinMessage().forEach(s -> event.getPlayer().sendMessage(Color.translate(s.replace("%PLAYER%", event.getPlayer().getDisplayName())))));
                } else {
                    Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> StringUtil.sendCenteredMessage(event.getPlayer(), (ArrayList<String>) MANAGER.getJoinMessage()));
                }
            }

            if (potPlayer.isAutoVanish()) {
                potPlayer.getPlayer().sendMessage(Color.translate(CorePlugin.getInstance().getServerManager().getAutomaticallyPutInto().replace("<value>", "vanish")));

                CorePlugin.getInstance().getPlayerManager().vanishPlayerRaw(event.getPlayer());
            }

            if (potPlayer.isAutoModMode()) {
                potPlayer.getPlayer().sendMessage(Color.translate(CorePlugin.getInstance().getServerManager().getAutomaticallyPutInto().replace("<value>", "mod mode")));

                CorePlugin.getInstance().getPlayerManager().modModeRaw(event.getPlayer());
            }

            if (event.getPlayer().hasPermission("scandium.staff")) {
                Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onConnect(event.getPlayer()))), 10L);

                serverManager.getStaffInformation().forEach(s -> event.getPlayer().sendMessage(s
                        .replace("<nice_char>", Character.toString('»'))
                        .replace("<channel>", ChatColor.RED + "None")
                        .replace("<messages>", (potPlayer.isCanSeeStaffMessages() ? ChatColor.GREEN + "Shown" : ChatColor.RED + "Hidden"))
                        .replace("<filter>", (potPlayer.isCanSeeFiltered() ? ChatColor.GREEN + "Shown" : ChatColor.RED + "Hidden"))
                        .replace("<modmode>", (potPlayer.isStaffMode() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"))
                        .replace("<vanish>", (potPlayer.isVanished() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"))
                ));
            }
        });
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

        if (CorePlugin.getInstance().getServerManager().isChatEnabled()) {
            if (!potPlayer.isCurrentlyMuted()) {
                checkChannel(event, player, potPlayer);
            } else {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Color.translate(PunishmentStrings.MUTE_MESSAGE));
            }
        } else {
            if (player.hasPermission("scandium.chat.bypass")) {
                if (!potPlayer.isCurrentlyMuted()) {
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
        event.setCancelled(true);
        if (event.getMessage().startsWith("!") && event.getPlayer().hasPermission(ChatChannelType.STAFF.getPermission())) {
            event.setCancelled(true);
            CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onChatChannel(ChatChannelType.STAFF, event.getMessage().replace("!", ""), event.getPlayer())));
        } else if (event.getMessage().startsWith("#") && event.getPlayer().hasPermission(ChatChannelType.ADMIN.getPermission())) {
            event.setCancelled(true);
            CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onChatChannel(ChatChannelType.ADMIN, event.getMessage().replace("#", ""), event.getPlayer())));
        } else if (event.getMessage().startsWith("$") && event.getPlayer().hasPermission(ChatChannelType.DEV.getPermission())) {
            event.setCancelled(true);
            CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onChatChannel(ChatChannelType.DEV, event.getMessage().replace("$", ""), event.getPlayer())));
        } else if (event.getMessage().startsWith("@") && event.getPlayer().hasPermission(ChatChannelType.HOST.getPermission())) {
            event.setCancelled(true);
            CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onChatChannel(ChatChannelType.HOST, event.getMessage().replace("@", ""), event.getPlayer())));
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
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer());
        if (potPlayer.isFrozen()) {
            event.setCancelled(true);
            return;
        }

        if (event.getMessage().contains(":") && !event.getPlayer().isOp()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Color.translate("&cThat syntax is not accepted!"));
        }

        if (CorePlugin.getInstance().getConfig().getBoolean("block-commands.enabled")) {
            if (event.getPlayer().hasPermission("scandium.protocol.bypass")) return;
            CorePlugin.getInstance().getConfig().getStringList("block-commands.list").forEach(s -> {
                if (event.getMessage().startsWith("/" + s)) {
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

                return;
            }
        }

        if (CorePlugin.ANTI_CMD_SPAM)
            CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer()).setCommandCooldown(System.currentTimeMillis() + 1L);
        else CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer()).setCommandCooldown(0L);
    }

    private void checkThenSend(AsyncPlayerChatEvent event, Player player, PotPlayer potPlayer, long slowChat) {
        Bukkit.getOnlinePlayers().forEach(player1 -> {
            PotPlayer potPlayer1 = CorePlugin.getInstance().getPlayerManager().getPlayer(player1);
            if (potPlayer1.isIgnoring(potPlayer.getPlayer())) {
                if (potPlayer1.isCanSeeGlobalChat()) {
                    player1.sendMessage(Color.translate(CorePlugin.CHAT_FORMAT
                            .replace("<prefix>", (potPlayer.getAppliedPrefix() != null ? potPlayer.getAppliedPrefix().getPrefix() + " " : ""))
                            .replace("<rank_prefix>", potPlayer.getActiveGrant().getRank().getPrefix())
                            .replace("<rank_suffix>", potPlayer.getActiveGrant().getRank().getSuffix())
                            .replace("<rank_color>", potPlayer.getActiveGrant().getRank().getColor())
                            .replace("<custom_color>", (potPlayer.getCustomColor() != null ? potPlayer.getCustomColor().toString() : ""))
                            .replace("<player_name>", player.getName())
                            .replace("<message>", event.getMessage())
                    ));
                }
            }
        });

        if (CorePlugin.ANTI_CHAT_SPAM)
            CorePlugin.getInstance().getPlayerManager().getPlayer(player).setChatCooldown(System.currentTimeMillis() + (slowChat > 0L ? slowChat : 3000L));
        else CorePlugin.getInstance().getPlayerManager().getPlayer(player).setChatCooldown(0L);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        if (event.getPlayer().hasPermission("scandium.staff"))
            CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onDisconnect(event.getPlayer())));

        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (potPlayer.isFrozen())
            CorePlugin.getInstance().getPlayerManager().sendDisconnectFreezeMessage(event.getPlayer());

        CorePlugin.getInstance().getServerManager().getVanishedPlayers().remove(event.getPlayer());
        potPlayer.savePlayerData();
    }
}
