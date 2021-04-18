package com.solexgames.core.player;

import com.google.gson.annotations.SerializedName;
import com.mojang.authlib.GameProfile;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.board.ScoreBoard;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.enums.LanguageType;
import com.solexgames.core.player.media.Media;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.player.achievement.AchievementData;
import com.solexgames.core.player.prefixes.Prefix;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentStrings;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.player.global.NetworkPlayer;
import com.solexgames.core.util.*;
import com.solexgames.core.util.rainbow.RainbowNametag;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Getter
@Setter
public class PotPlayer {

    private List<Punishment> punishments = new ArrayList<>();
    private List<Grant> allGrants = new ArrayList<>();
    private List<String> allPrefixes = new ArrayList<>();
    private List<String> allIgnoring = new ArrayList<>();
    private List<String> allFriends = new ArrayList<>();
    private List<String> userPermissions = new ArrayList<>();

    @SerializedName("_id")
    private UUID uuid;
    private Player player;
    private String ipAddress;
    private String encryptedIpAddress;
    private String previousIpAddress;
    private Media media;
    private Prefix appliedPrefix;

    private Player lastRecipient;

    private ChatColor customColor;

    private String rankName;
    private String syncCode;
    private String syncDiscord;
    private String name;

    private String authSecret;
    private Document profile;

    private boolean canSeeStaffMessages = true;
    private boolean canSeeGlobalChat = true;
    private boolean canReceiveDms = true;
    private boolean canReceiveDmsSounds = true;
    private boolean canSeeBroadcasts = true;
    private boolean canSeeFiltered = true;
    private boolean canSeeTips = true;
    private boolean canAcceptingFriendRequests = true;
    private boolean canReport = true;
    private boolean canRequest = true;

    private boolean hasVoted = false;
    private boolean hasActiveWarning = false;
    private boolean hasSetup2FA = false;

    private boolean isVanished = false;
    private boolean isStaffMode = false;
    private boolean isFrozen = false;
    private boolean isSynced = false;
    private boolean isLoaded = false;
    private boolean isSocialSpy = false;
    private boolean isAutoVanish = false;
    private boolean isAutoModMode = false;

    private boolean currentlyMuted;
    private boolean currentlyRestricted;
    private boolean currentlyBlacklisted;
    private boolean currentlyOnline;

    private boolean relatedToBlacklist;
    private String relatedTo;

    private ItemStack[] itemHistory;
    private ItemStack[] armorHistory;

    private Scoreboard previousBoard;
    private ScoreBoard modModeBoard;

    private boolean isGrantEditing = false;
    private Document grantTarget = null;
    private Rank grantRank = null;
    private long grantDuration;
    private boolean grantPerm;
    private String grantScope;

    private ChatChannelType channel;

    private boolean isGrantDurationEditing = false;
    private Document grantDurationTarget = null;
    private Rank grantDurationRank = null;
    private boolean grantDurationPerm;
    private String grantDurationScope;

    private LanguageType language;
    private PermissionAttachment attachment;
    private GameProfile gameProfile;

    private long chatCooldown = 1L;
    private long commandCooldown = 1L;

    private int lastItemSlot;
    private ItemStack lastItem;

    private Date lastJoined;

    private String lastJoin;
    private String firstJoin;

    private Punishment warningPunishment;
    private Punishment restrictionPunishment;
    private AchievementData achievementData;

    private Rank disguiseRank;
    private RainbowNametag rainbowNametag;

    private int experience;

    private boolean hasLoaded;

    public PotPlayer(UUID uuid, String name, InetAddress inetAddress) {
        this.uuid = uuid;
        this.ipAddress = inetAddress.getHostAddress();
        this.name = name;

        this.media = new Media();
        this.lastJoined = new Date();
        this.syncCode = SaltUtil.getRandomSaltedString(6);
        this.hasLoaded = false;

        CompletableFuture.runAsync(this::loadPlayerData);
        CompletableFuture.runAsync(() -> this.encryptedIpAddress = CorePlugin.getInstance().getCryptoManager().encrypt(this.ipAddress));

        CorePlugin.getInstance().getPlayerManager().getAllProfiles().put(uuid, this);
    }

    public Document getDocument(boolean removing) {
        final Document document = new Document("_id", this.uuid);

        document.put("name", this.getName());
        document.put("uuid", this.uuid.toString());
        document.put("hasSetup2FA", this.hasSetup2FA);
        document.put("securityKey", this.authSecret);
        document.put("canSeeStaffMessages", this.canSeeStaffMessages);
        document.put("canSeeTips", this.canSeeTips);
        document.put("canReceiveDms", this.canReceiveDms);
        document.put("canSeeGlobalChat", this.canSeeGlobalChat);
        document.put("canSeeFiltered", this.canSeeFiltered);
        document.put("canReceiveDmsSounds", this.canReceiveDmsSounds);
        document.put("canSeeBroadcasts", this.canSeeBroadcasts);
        document.put("lastJoined", CorePlugin.FORMAT.format(new Date()));
        document.put("firstJoined", this.firstJoin);
        document.put("disguiseRank", (this.disguiseRank == null ? null : this.disguiseRank.getName()));

        final List<String> grantStrings = new ArrayList<>();
        this.getAllGrants().forEach(grant -> grantStrings.add(grant.toJson()));

        final List<String> prefixStrings = new ArrayList<>(this.getAllPrefixes());

        document.put("allGrants", grantStrings);
        document.put("allPrefixes", prefixStrings);
        document.put("allPermissions", userPermissions);
        document.put("allIgnored", this.allIgnoring);

        if (this.appliedPrefix != null) {
            document.put("appliedPrefix", this.appliedPrefix.getName());
        } else {
            document.put("appliedPrefix", "Default");
        }
        document.put("rankName", this.getActiveGrant().getRank().getName());
        if (this.customColor != null) {
            document.put("customColor", this.customColor.name());
        } else {
            document.put("customColor", null);
        }

        document.put("discordSyncCode", this.syncCode);
        document.put("syncDiscord", this.syncDiscord);
        document.put("isSynced", this.isSynced);
        document.put("hasVoted", this.hasVoted);
        document.put("language", (this.language != null ? this.language.getLanguageName() : LanguageType.ENGLISH.getLanguageName()));
        document.put("currentlyOnline", !removing);

        document.put("discord", this.media.getDiscord());
        document.put("twitter", this.media.getTwitter());
        document.put("instagram", this.media.getInstagram());
        document.put("youtube", this.media.getYoutubeLink());

        document.put("achievementData", CorePlugin.GSON.toJson(this.achievementData));

        document.put("autoVanish", this.isAutoVanish);
        document.put("autoModMode", this.isAutoModMode);
        document.put("previousIpAddress", this.encryptedIpAddress);
        document.put("experience", this.experience);
        document.put("blacklisted", this.currentlyBlacklisted);
        document.put("restricted", this.currentlyRestricted);

        return document;
    }

    public void saveWithoutRemove() {
        CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", uuid), this.getDocument(false), new ReplaceOptions().upsert(true)));
    }

    public void savePlayerData() {
        RedisUtil.writeAsync(RedisUtil.removeGlobalPlayer(this.uuid));

        CorePlugin.getInstance().getPlayerManager().getAllNetworkProfiles().remove(this.uuid);
        CorePlugin.getInstance().getPlayerManager().getAllProfiles().remove(this.uuid);

        CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", uuid), this.getDocument(true), new ReplaceOptions().upsert(true)));
    }

    public void loadPlayerData() {
        CompletableFuture.runAsync(() -> {
            CorePlugin.getInstance().getPunishmentManager().getPunishments().stream()
                    .filter(punishment -> punishment.getTarget().equals(this.uuid))
                    .forEach(this.punishments::add);

            this.getPunishments().stream()
                    .filter(punishment -> punishment != null && punishment.isActive() && (System.currentTimeMillis() >= punishment.getCreatedAt().getTime() + punishment.getPunishmentDuration() || punishment.isPermanent()) && !punishment.isRemoved())
                    .forEach(punishment -> {
                        switch (punishment.getPunishmentType()) {
                            case WARN:
                                this.hasActiveWarning = true;
                                this.warningPunishment = punishment;
                                break;
                            case MUTE:
                                this.currentlyMuted = true;
                                break;
                            case BLACKLIST:
                                this.restrictionPunishment = punishment;
                                this.currentlyBlacklisted = true;
                                this.currentlyRestricted = true;
                                break;
                            case IP_BAN:
                            case BAN:
                                this.currentlyRestricted = true;
                                this.restrictionPunishment = punishment;
                                break;
                        }
                    });
        });

        CompletableFuture.supplyAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("_id", this.uuid)).first()).thenAcceptAsync(document -> {
            this.profile = document;

            if (this.profile == null) {
                this.saveWithoutRemove();
                this.hasLoaded = true;

                System.out.println("[DEBUG] A document was null.");

                return;
            }

            this.name = this.getName();
            if (profile.getBoolean("canSeeStaffMessages") != null) {
                this.canSeeStaffMessages = profile.getBoolean("canSeeStaffMessages");
            }
            if (profile.getBoolean("canSeeTips") != null) {
                this.canSeeTips = profile.getBoolean("canSeeTips");
            }
            if (profile.getBoolean("canReceiveDms") != null) {
                this.canReceiveDms = profile.getBoolean("canReceiveDms");
            }
            if (profile.getBoolean("canSeeFiltered") != null) {
                this.canSeeFiltered = profile.getBoolean("canSeeFiltered");
            }
            if (profile.getBoolean("canSeeBroadcasts") != null) {
                this.canSeeBroadcasts = profile.getBoolean("canSeeBroadcasts");
            }
            if (profile.getBoolean("canSeeGlobalChat") != null) {
                this.canSeeGlobalChat = profile.getBoolean("canSeeGlobalChat");
            }
            if (profile.getBoolean("hasVoted") != null) {
                this.hasVoted = profile.getBoolean("hasVoted");
            }
            if (profile.getBoolean("autoModMode") != null) {
                this.isAutoModMode = profile.getBoolean("autoModMode");
            }
            if (profile.getString("previousIpAddress") != null) {
                this.previousIpAddress = CorePlugin.getInstance().getCryptoManager().decrypt(profile.getString("previousIpAddress"));
            } else {
                this.previousIpAddress = "";
            }
            if (profile.getString("privateKey") != null) {
                this.authSecret = profile.getString("privateKey");
            }
            if (profile.getBoolean("autoVanish") != null) {
                this.isAutoVanish = profile.getBoolean("autoVanish");
            }
            if (profile.getBoolean("canReceiveDmsSounds") != null) {
                this.canReceiveDmsSounds = profile.getBoolean("canReceiveDmsSounds");
            }
            if (profile.getBoolean("hasSetup2FA") != null) {
                this.hasSetup2FA = profile.getBoolean("hasSetup2FA");
            } else {
                this.hasSetup2FA = false;
            }
            if (profile.getString("firstJoined") != null) {
                this.firstJoin = profile.getString("firstJoined");
            } else {
                this.firstJoin = CorePlugin.FORMAT.format(new Date());
            }
            if (profile.getString("language") != null) {
                this.language = LanguageType.getByName(profile.getString("language"));
            } else {
                this.language = LanguageType.ENGLISH;
            }
            if (profile.getInteger("experience") != null) {
                this.experience = profile.getInteger("experience");
            } else {
                this.experience = 0;
            }
            if (profile.getString("customColor") != null) {
                this.customColor = ChatColor.valueOf(profile.getString("customColor"));
            }
            if (profile.getString("discord") != null) {
                this.media.setDiscord(profile.getString("discord"));
            } else {
                this.media.setDiscord("N/A");
            }
            if (profile.getString("twitter") != null) {
                this.media.setTwitter(profile.getString("twitter"));
            } else {
                this.media.setTwitter("N/A");
            }
            if (profile.getString("youtube") != null) {
                this.media.setYoutubeLink(profile.getString("youtube"));
            } else {
                this.media.setYoutubeLink("N/A");
            }
            if (profile.getString("instagram") != null) {
                this.media.setInstagram(profile.getString("instagram"));
            } else {
                this.media.setInstagram("N/A");
            }
            if (profile.getList("allGrants", String.class).isEmpty()) {
                this.allGrants.add(new Grant(null, Objects.requireNonNull(Rank.getDefault()), new Date().getTime(), -1L, "Automatic Grant (Default)", true, true));
            } else {
                final List<String> allGrants = profile.getList("allGrants", String.class);
                allGrants.forEach(s -> this.allGrants.add(CorePlugin.GSON.fromJson(s, Grant.class)));
            }
            if ((profile.getString("appliedPrefix") != null) && !profile.getString("appliedPrefix").equals("Default")) {
                this.appliedPrefix = Prefix.getByName(profile.getString("appliedPrefix"));
            } else {
                this.appliedPrefix = null;
            }
            if (profile.get("allIgnored") != null) {
                final List<String> ignoring = profile.getList("allIgnored", String.class);

                if (!ignoring.isEmpty()) {
                    this.allIgnoring.addAll(ignoring);
                }
            }
            if (profile.get("allPermissions") != null) {
                final List<String> permissions = profile.getList("allPermissions", String.class);

                if (!permissions.isEmpty()) {
                    this.userPermissions.addAll(permissions);
                }
            }
            if (profile.getBoolean("isSynced") != null) {
                this.setSynced(profile.getBoolean("isSynced"));
            }
            if (profile.getString("syncDiscord") != null) {
                this.setSyncDiscord(profile.getString("syncDiscord"));
            }
            if (profile.getString("discordSyncCode") != null) {
                this.setSyncCode(profile.getString("discordSyncCode"));
            } else {
                this.setSyncCode(SaltUtil.getRandomSaltedString());
            }
            if (profile.getString("achievementData") != null) {
                this.achievementData = CorePlugin.GSON.fromJson(profile.getString("achievementData"), AchievementData.class);
            } else {
                this.achievementData = new AchievementData();
            }

            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () ->
                    new NetworkPlayer(this.uuid, this.name, CorePlugin.getInstance().getServerName(), this.getActiveGrant().getRank().getName(), this.isCanReceiveDms(), this.ipAddress, this.syncCode, this.isSynced)
            );

            RedisUtil.writeAsync(RedisUtil.addGlobalPlayer(this));
            CorePlugin.getInstance().getPlayerManager().getAllSyncCodes().put(this.syncCode, this.getName());

            this.currentlyOnline = true;
            this.hasLoaded = true;
        });

        Bukkit.getScheduler().runTaskLaterAsynchronously(CorePlugin.getInstance(), this::saveWithoutRemove, 60L);
    }

    public boolean findIpRelative(AsyncPlayerPreLoginEvent loginEvent, boolean hub) {
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        CompletableFuture.supplyAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("previousIpAddress", this.encryptedIpAddress)).iterator())
                .thenAcceptAsync(documentIterator -> {
                    while (documentIterator.hasNext()) {
                        final Document document = documentIterator.next();

                        if (document.getBoolean("blacklisted") != null && document.getBoolean("blacklisted") && !this.currentlyBlacklisted) {
                            this.currentlyBlacklisted = true;
                            this.currentlyRestricted = true;
                            this.relatedToBlacklist = true;
                            this.relatedTo = document.getString("name");

                            final Date date = new Date();
                            final Punishment punishment = new Punishment(
                                    PunishmentType.BLACKLIST,
                                    null,
                                    this.uuid,
                                    this.name,
                                    "Related to Blacklisted Player (" + this.relatedTo + ")",
                                    date,
                                    date.getTime() - DateUtil.parseDateDiff("1d", false),
                                    true,
                                    date,
                                    UUID.randomUUID(),
                                    SaltUtil.getRandomSaltedString(7),
                                    true
                            );

                            punishment.savePunishment();

                            this.restrictionPunishment = punishment;

                            if (!hub) {
                                loginEvent.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Color.translate(PunishmentStrings.BLACK_LIST_RELATION_MESSAGE.replace("<player>", document.getString("name"))));
                            }

                            atomicBoolean.set(true);

                            return;
                        }
                    }
                });

        return atomicBoolean.get();
    }

    public void onAfterDataLoad() {
        this.player = Bukkit.getPlayer(uuid);
        this.attachment = this.player.addAttachment(JavaPlugin.getPlugin(CorePlugin.class));
        this.gameProfile = CorePlugin.getInstance().getPlayerManager().getGameProfile(this.player);
        this.rainbowNametag = new RainbowNametag(this.player, CorePlugin.getInstance());

        CompletableFuture.runAsync(() -> {
            if (CorePlugin.getInstance().getServerSettings().isNameMcEnabled()) {
                this.checkVoting();
            }

            this.setupPlayer();

            if (this.profile.get("allPrefixes") != null) {
                if (this.player.hasPermission("scandium.prefixes.all")) {
                    final List<String> prefixes = new ArrayList<>();

                    Prefix.getPrefixes().forEach(prefix -> prefixes.add(prefix.getName()));
                    this.allPrefixes.addAll(prefixes);
                } else {
                    final List<String> prefixes = profile.getList("allPrefixes", String.class);

                    this.allPrefixes.addAll(prefixes);
                }
            }

            if (this.player.hasPermission("scandium.staff") && !CorePlugin.getInstance().getPlayerManager().isOnline(this.player.getName())) {
                RedisUtil.writeAsync(RedisUtil.onConnect(this.player));
            }
        });
    }

    public String getWarningMessage() {
        return ChatColor.RED + "You currently have an active warning for " + this.warningPunishment.getReason() + ".\n" + ChatColor.RED + "This warning will expire in " + ChatColor.RED + ChatColor.BOLD.toString() + this.warningPunishment.getDurationString() + ChatColor.RED + ".";
    }

    public String getRestrictionMessage() {
        if (this.relatedToBlacklist) {
            return Color.translate(PunishmentStrings.BLACK_LIST_RELATION_MESSAGE.replace("<player>", this.relatedTo));
        }

        switch (this.restrictionPunishment.getPunishmentType()) {
            case BLACKLIST:
                return Color.translate(PunishmentStrings.BLACK_LIST_MESSAGE.replace("<reason>", this.restrictionPunishment.getReason()));
            case IP_BAN:
            case BAN:
                return (this.restrictionPunishment.isPermanent() ? Color.translate(PunishmentStrings.BAN_MESSAGE_PERM.replace("<reason>", this.restrictionPunishment.getReason())) : Color.translate(PunishmentStrings.BAN_MESSAGE_TEMP.replace("<reason>", this.restrictionPunishment.getReason()).replace("<time>", this.restrictionPunishment.getDurationString())));
            default:
                return "";
        }
    }

    public Grant getActiveGrant() {
        return this.getAllGrants().stream()
                .sorted(Comparator.comparingLong(Grant::getDateAdded).reversed())
                .collect(Collectors.toList()).stream()
                .filter(grant -> grant != null && grant.isActive() && !grant.getRank().isHidden() && (grant.getScope() == null || grant.isGlobal() || grant.isApplicable()))
                .findFirst()
                .orElseGet(this::getDefaultGrant);
    }

    public Grant getDefaultGrant() {
        return new Grant(null, Objects.requireNonNull(Rank.getDefault()), System.currentTimeMillis(), -1L, "Automatic Grant (Default)", true, true);
    }

    public void setupPlayer() {
        this.resetPermissions();
        this.setupPermissions();
        this.setupDisplay();

        Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> {
            this.setupPlayerTag();
            this.setupPlayerList();
        }, 38L);
    }

    public void setupDisplay() {
        this.player.setDisplayName(Color.translate(this.getActiveGrant().getRank().getColor()) + this.player.getName());
    }

    public void setupPlayerList() {
        this.player.setPlayerListName(Color.translate((this.getActiveGrant().getRank().getColor() == null ? ChatColor.GRAY.toString() : this.getActiveGrant().getRank().getColor()) + (this.customColor != null ? this.customColor : "") + this.player.getName()));

        if (CorePlugin.getInstance().getServerSettings().isTabEnabled()) {
            CorePlugin.getInstance().getNMS().updateTablist();
        }
    }

    public void resetPermissions() {
        if (!this.attachment.getPermissions().isEmpty()) {
            this.attachment.getPermissions().keySet().forEach(s -> this.attachment.unsetPermission(s));
        }
    }

    public void setupPermissions() {
        CompletableFuture.runAsync(() -> {
            this.getAllGrants().stream()
                    .filter(grant -> grant != null && grant.isActive() && (grant.isApplicable() || grant.isGlobal()))
                    .forEach(grant -> {
                        grant.getRank().getPermissions().forEach(s -> this.attachment.setPermission(s.replace("*", ""), !s.startsWith("*")));
                        grant.getRank().getInheritance().stream().map(Rank::getByUuid).filter(Objects::nonNull).forEach(rank -> rank.getPermissions().forEach(s -> this.attachment.setPermission(s.replace("*", ""), !s.startsWith("*"))));
                    });
            this.getUserPermissions().forEach(s -> this.attachment.setPermission(s.replace("*", ""), !s.startsWith("*")));
        });

        this.player.recalculatePermissions();
    }

    public void setupPlayerTag() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (this.isStaffMode()) {
                if (player.hasPermission("scandium.staff")) {
                    CorePlugin.getInstance().getNameTagManager().setupStaffModeTag(player, this.player);
                    return;
                }
            } else if (this.isVanished()) {
                if (player.hasPermission("scandium.staff")) {
                    CorePlugin.getInstance().getNameTagManager().setupVanishTag(player, this.player);
                    return;
                }
            }

            CorePlugin.getInstance().getNameTagManager().setupNameTag(player, this.player, this.getColorByRankColor());

            final PotPlayer potPlayer = CorePlugin.getInstance().getPlayerManager().getPlayer(player);
            CorePlugin.getInstance().getNameTagManager().setupNameTag(this.player, player, potPlayer.getColorByRankColor());
        });
    }

    public boolean isAuthValid(int code) {
        if (this.authSecret == null) {
            return false;
        }

        if (this.authSecret.equals("")) {
            return false;
        }

        try {
            return TotpUtil.validateCurrentNumber(this.authSecret, code, 250);
        } catch (Exception ignored) {
            return false;
        }
    }

    public boolean isAuthValid(String secret, int code) {
        if (secret == null) {
            return false;
        }

        if (secret.equals("")) {
            return false;
        }

        try {
            return TotpUtil.validateCurrentNumber(secret, code, 250);
        } catch (Exception ignored) {
            return false;
        }
    }

    public ChatColor getColorByRankColor() {
        if (this.disguiseRank != null) {
            return ChatColor.getByChar(this.getDisguiseRank().getColor().replace("&", "").replace("§", ""));
        } else {
            return ChatColor.getByChar(this.getActiveGrant().getRank().getColor().replace("&", "").replace("§", ""));
        }
    }

    public Grant getByDate(long date) {
        return this.getAllGrants().stream().filter(grant -> grant.getDateAdded() == date).findFirst().orElse(null);
    }

    public Grant getById(String id) {
        return this.getAllGrants().stream().filter(grant -> grant.getId().equals(id)).findFirst().orElse(null);
    }

    public boolean isIgnoring(Player player) {
        return !this.getAllIgnoring().contains(player.getName());
    }

    public void checkVoting() {
        CompletableFuture.runAsync(() -> {
            if (!this.hasVoted) {
                if (VotingUtil.hasVoted(this.uuid.toString())) {
                    this.hasVoted = true;
                    this.getAllPrefixes().add("Liked");

                    if (this.getAppliedPrefix() == null) this.appliedPrefix = Prefix.getByName("Liked");
                    if (this.player != null) {
                        this.player.sendMessage(ChatColor.GREEN + Color.translate("Thanks for voting for us on &6NameMC&a!"));
                        this.player.sendMessage(ChatColor.GREEN + Color.translate("You've been granted the &b✔ &7(Liked)&a prefix!"));
                    }
                }
            } else {
                if (!VotingUtil.hasVoted(this.uuid.toString())) {
                    this.hasVoted = false;
                    this.getAllPrefixes().remove("Liked");

                    this.player.sendMessage(ChatColor.RED + Color.translate("Your permission to access the &b✔ &7(Liked) &ctag has been revoked as You've unliked our server on NameMC!"));
                    this.player.sendMessage(ChatColor.RED + Color.translate("To gain your tag back, like us on NameMC again!"));
                }
            }
        });
    }
}
