package vip.potclub.core.listener;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
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
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.ChatChannelType;
import vip.potclub.core.manager.ServerManager;
import vip.potclub.core.media.MediaConstants;
import vip.potclub.core.menu.IMenu;
import vip.potclub.core.menu.extend.grant.GrantSelectConfirmMenu;
import vip.potclub.core.menu.extend.grant.GrantSelectReasonMenu;
import vip.potclub.core.menu.extend.punish.PunishSelectDurationMenu;
import vip.potclub.core.player.PotPlayer;
import vip.potclub.core.player.grant.Grant;
import vip.potclub.core.player.punishment.Punishment;
import vip.potclub.core.player.punishment.PunishmentStrings;
import vip.potclub.core.util.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;

public class PlayerListener implements Listener {

    public static String REGION = CorePlugin.getInstance().getConfig().getString("region");
    public ServerManager MANAGER = CorePlugin.getInstance().getServerManager();

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        if (CorePlugin.getInstance().getWhitelistConfig().getBoolean("whitelist")) {
            event.setMotd(Color.translate("&d&lPotClub &7&l┃ &f" + REGION + " Region\n&cThe server is currently in maintenance."));
        } else {
            int boundOfThree = CorePlugin.RANDOM.nextInt(3);
            if (boundOfThree == 1) {
                event.setMotd(Color.translate("&d&lPotClub &7&l┃ &f" + REGION + " Region\n&7Join our discord via &bhttps://dsc.gg/pot&7!"));
            } else if (boundOfThree == 2) {
                event.setMotd(Color.translate("&d&lPotClub &7&l┃ &f" + REGION + " Region\n&7Follow our twitter for giveaways and more! &b@PotClubVIP&7"));
            } else {
                event.setMotd(Color.translate("&d&lPotClub &7&l┃ &f" + REGION + " Region\n&7We have a new website! Check it out at &bwww.potclub.vip&7!"));
            }
        }
    }

    @EventHandler
    public void onConnect(AsyncPlayerPreLoginEvent event) {
        if (CorePlugin.CAN_JOIN) {
            /*if (CorePlugin.getInstance().getWhitelistConfig().getConfiguration().getBoolean("beta-whitelist")) {
                if (!CorePlugin.getInstance().getServerManager().getBetaWhitelistedPlayers().contains(event.getName())) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', CorePlugin.getInstance().getWhitelistConfig().getConfiguration().getString("whitelisted-msg").replace("<nl>", "\n")));
                } else {
                    checkDisallow(event);
                    return;
                }
            } else checkDisallow(event);*/
            if (CorePlugin.getInstance().getWhitelistConfig().getConfiguration().getBoolean("whitelist")) {
                if (!CorePlugin.getInstance().getServerManager().getWhitelistedPlayers().contains(event.getName())) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', CorePlugin.getInstance().getWhitelistConfig().getConfiguration().getString("whitelisted-msg").replace("<nl>", "\n")));

                } else checkDisallow(event);
            } else checkDisallow(event);
        } else event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Color.translate("&cThe server is currently booting...\n&cPlease reconnect in a few minutes."));
    }

    private void checkDisallow(AsyncPlayerPreLoginEvent event) {
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

        if (potPlayer.isFrozen()) {
            event.setCancelled(true);
            event.getPlayer().teleport(event.getFrom());
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
        new PotPlayer(event.getPlayer().getUniqueId());

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

        if (event.getPlayer().hasPermission("scandium.staff")) Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onConnect(event.getPlayer()))), 10L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);

        if (potPlayer.isFrozen()) {
            event.setCancelled(true);

            Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(player1 -> player1.hasPermission("scandium.staff"))
                    .filter(player1 -> CorePlugin.getInstance().getPlayerManager().getPlayer(player1).isCanSeeStaffMessages())
                    .forEach(player1 -> player1.sendMessage(Color.translate("&b[S] &7[Frozen] &b" + potPlayer.getPlayer().getDisplayName() + "&f: &f" + event.getMessage())));

            event.getPlayer().sendMessage(Color.translate(Color.translate("&7[Frozen] &b" + potPlayer.getPlayer().getDisplayName() + "&f: &f" + event.getMessage())));
            return;
        }

        Matcher discordMatcher = MediaConstants.DISCORD_USERNAME_REGEX.matcher(event.getMessage());
        Matcher twitterMatcher = MediaConstants.TWITTER_USERNAME_REGEX.matcher(event.getMessage());
        Matcher instaMatcher = MediaConstants.INSTAGRAM_USERNAME_REGEX.matcher(event.getMessage());
        Matcher youtubeMatcher = MediaConstants.YOUTUBE_PROFILELINK_REGEX.matcher(event.getMessage());

        if (potPlayer.isGrantDurationEditing()) {
            event.setCancelled(true);
            String message = event.getMessage();

            if (event.getMessage().equalsIgnoreCase("cancel")) {
                player.sendMessage(Color.translate("&cCancelled the granting process."));

                potPlayer.setGrantDurationRank(null);
                potPlayer.setGrantDurationTarget(null);
                potPlayer.setGrantDurationEditing(false);
            } else if (message.equalsIgnoreCase("perm") || message.equalsIgnoreCase("permanent")) {
                new GrantSelectReasonMenu(player, potPlayer.getGrantDurationTarget(), -1L, potPlayer.getGrantDurationRank(), true).open(player);

                potPlayer.setGrantDurationRank(null);
                potPlayer.setGrantDurationTarget(null);
                potPlayer.setGrantDurationEditing(false);
            } else {
                try {
                    new GrantSelectReasonMenu(player, potPlayer.getGrantDurationTarget(), System.currentTimeMillis() - DateUtil.parseDateDiff(event.getMessage(), false), potPlayer.getGrantDurationRank(), false).open(player);

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
            String message = event.getMessage();

            if (event.getMessage().equalsIgnoreCase("cancel")) {
                player.sendMessage(Color.translate("&cCancelled the granting process."));
                potPlayer.setGrantTarget(null);
                potPlayer.setGrantRank(null);
                potPlayer.setGrantPerm(false);
            } else {
                player.sendMessage(Color.translate("&aSet the grant reason to &6'" + message + "'&a."));
                new GrantSelectConfirmMenu(potPlayer.getPlayer(), potPlayer.getGrantTarget(), potPlayer.getGrantRank(), potPlayer.getGrantDuration(), message, potPlayer.isGrantPerm()).open(player);
            }
            potPlayer.setGrantEditing(false);
            return;
        }

        if (potPlayer.isReasonEditing()) {
            event.setCancelled(true);
            String message = event.getMessage();

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

        if (CorePlugin.getInstance().getServerManager().isChatEnabled()) {
            if (!CorePlugin.getInstance().getPlayerManager().getPlayer(player).isCurrentlyMuted()) {
                checkChannel(event, player, potPlayer);
            } else {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Color.translate(PunishmentStrings.MUTE_MESSAGE));
            }
        } else {
            if (player.hasPermission("scandium.chat.bypass")) {
                if (!CorePlugin.getInstance().getPlayerManager().getPlayer(player).isCurrentlyMuted()) {
                    checkChannel(event, player, potPlayer);
                } else {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Color.translate(PunishmentStrings.MUTE_MESSAGE));
                }
            } else {
                event.setCancelled(true);
                switch (CorePlugin.getInstance().getPlayerManager().getPlayer(player).getLanguage()) {
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

        if (CorePlugin.getInstance().getConfig().getBoolean("block-commands.enabled")) {
            if (event.getPlayer().hasPermission("scandium.protocol.bypass")) return;
            CorePlugin.getInstance().getConfig().getStringList("block-commands.list").forEach(s -> {
                if (event.getMessage().startsWith("/" + s)) {
                    event.getPlayer().sendMessage(Color.translate(CorePlugin.getInstance().getConfig().getString("block-commands.message")));
                    event.setCancelled(true);
                }
            });
        }
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

        if (CorePlugin.ANTI_CHAT_SPAM) CorePlugin.getInstance().getPlayerManager().getPlayer(player).setChatCooldown(System.currentTimeMillis() + (slowChat > 0L ? slowChat : 3000L)); else CorePlugin.getInstance().getPlayerManager().getPlayer(player).setChatCooldown(0L);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        if (event.getPlayer().hasPermission("scandium.staff"))
            CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisClient().write(RedisUtil.onDisconnect(event.getPlayer())));

        PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (potPlayer.isFrozen())
            CorePlugin.getInstance().getPlayerManager().sendDisconnectFreezeMessage(event.getPlayer());

        CorePlugin.getInstance().getServerManager().getVanishedPlayers().remove(event.getPlayer());
        potPlayer.savePlayerData();
    }
}
