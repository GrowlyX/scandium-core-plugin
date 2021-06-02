package com.solexgames.core.player;

import com.google.gson.annotations.SerializedName;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.board.ScoreBoard;
import com.solexgames.core.disguise.DisguiseData;
import com.solexgames.core.enums.ChatChannelType;
import com.solexgames.core.enums.LanguageType;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.player.media.Media;
import com.solexgames.core.player.meta.MetaDataEntry;
import com.solexgames.core.player.meta.MetaDataValue;
import com.solexgames.core.player.notes.Note;
import com.solexgames.core.player.prefixes.Prefix;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentStrings;
import com.solexgames.core.player.ranks.Rank;
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
import org.bukkit.scoreboard.Scoreboard;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Setter
public class PotPlayer {

    private List<Punishment> punishments = new ArrayList<>();
    private List<Grant> allGrants = new ArrayList<>();
    private List<Note> allNotes = new ArrayList<>();
    private List<String> allPrefixes = new ArrayList<>();
    private List<String> allIgnoring = new ArrayList<>();
    private List<String> userPermissions = new ArrayList<>();
    private List<String> bungeePermissions = new ArrayList<>();

    private Map<String, MetaDataEntry> metaDataEntryMap = new HashMap<>();

    @SerializedName("_id")
    private UUID uuid;
    private Player player;
    private String ipAddress;
    private String encryptedIpAddress;
    private String previousIpAddress;
    private Media media;
    private Prefix appliedPrefix;

    private String lastRecipient;

    private ChatColor customColor;

    private String rankName;
    private String syncCode;
    private String syncDiscord;
    private String name;
    private String originalName;

    private String authSecret;
    private Document profile;

    private boolean canSeeStaffMessages = true;
    private boolean canSeeGlobalChat = true;
    private boolean canReceiveDms = true;
    private boolean canReceiveDmsSounds = true;
    private boolean canSeeFiltered = true;
    private boolean canSeeTips = true;
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
    private boolean isDisguised = false;

    private boolean requiredToAuth = false;
    private boolean authBypassed = false;

    private boolean currentlyMuted;
    private boolean currentlyRestricted;
    private boolean currentlyIpRestricted;
    private boolean currentlyBlacklisted;
    private boolean currentlyOnline;

    private boolean relatedToBlacklist;
    private String relatedTo;

    private boolean relatedToIpBanned;
    private String relatedToIpBannedWho;

    private ItemStack[] itemHistory;
    private ItemStack[] armorHistory;

    private Scoreboard previousBoard;
    private ScoreBoard modModeBoard;
    private ChatChannelType channel;

    private LanguageType language;
    private PermissionAttachment attachment;

    private GameProfile gameProfile;
    private String skin;
    private String signature;

    private long chatCooldown = 1L;
    private long commandCooldown = 1L;

    private Date lastJoined;
    private String firstJoin;

    private Punishment warningPunishment;
    private Punishment restrictionPunishment;

    private Rank disguiseRank;
    private RainbowNametag rainbowNametag;

    private int experience;
    private long lastAuth = 0L;

    private boolean hasLoaded;

    public PotPlayer(UUID uuid, String name, InetAddress inetAddress) {
        this.uuid = uuid;
        this.ipAddress = (inetAddress != null ? inetAddress.getHostAddress() : "");
        this.name = name;
        this.originalName = name;

        this.media = new Media();
        this.lastJoined = new Date();
        this.hasLoaded = false;

        this.syncCode = SaltUtil.getRandomSaltedString(6);
        this.encryptedIpAddress = CorePlugin.getInstance().getCryptoManager().encrypt(this.ipAddress);

        this.loadPlayerData();

        CorePlugin.getInstance().getPlayerManager().getAllProfiles().put(uuid, this);
    }

    public Document getDocument(boolean removing) {
        final Document document = new Document("_id", this.uuid);

        document.put("name", this.getOriginalName());
        document.put("uuid", this.uuid.toString());
        document.put("securityKey", this.authSecret);
        document.put("canSeeStaffMessages", this.canSeeStaffMessages);
        document.put("canSeeTips", this.canSeeTips);
        document.put("canReceiveDms", this.canReceiveDms);
        document.put("canSeeGlobalChat", this.canSeeGlobalChat);
        document.put("canSeeFiltered", this.canSeeFiltered);
        document.put("canReceiveDmsSounds", this.canReceiveDmsSounds);
        document.put("lastJoined", CorePlugin.FORMAT.format(new Date()));
        document.put("firstJoined", this.firstJoin);
        document.put("disguiseRank", (this.disguiseRank == null ? null : this.disguiseRank.getName()));

        final Map<String, String> stringStringMap = new HashMap<>();

        this.getMetaDataEntryMap().forEach((k, v) -> stringStringMap.put(k, CorePlugin.GSON.toJson(v)));

        document.put("metaData", stringStringMap);

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
        document.put("currentlyDisguised", this.isDisguised);

        document.put("requiredToAuth", this.requiredToAuth);
        document.put("lastAuth", this.lastAuth);
        document.put("hasSetup2FA", this.hasSetup2FA);
        document.put("authBypassed", this.authBypassed);

        document.put("discord", this.media.getDiscord());
        document.put("twitter", this.media.getTwitter());
        document.put("instagram", this.media.getInstagram());
        document.put("youtube", this.media.getYoutubeLink());

        document.put("autoVanish", this.isAutoVanish);
        document.put("autoModMode", this.isAutoModMode);
        document.put("previousIpAddress", this.encryptedIpAddress);
        document.put("experience", this.experience);
        document.put("blacklisted", this.currentlyBlacklisted);
        document.put("restricted", this.currentlyRestricted);
        document.put("ipbanned", this.currentlyIpRestricted);
        document.put("currentlyOn", CorePlugin.getInstance().getServerName());

        return document;
    }

    public void saveWithoutRemove() {
        this.saveMeta();

        CompletableFuture.runAsync(() ->
                CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", this.uuid), this.getDocument(false), new ReplaceOptions().upsert(true))
        );
    }

    private void saveMeta() {
        this.setMetaData("chat-channel", new MetaDataValue(this.channel == null ? "NONE" : this.channel.name()));
        this.setMetaData("last-messenger", new MetaDataValue(this.lastRecipient == null ? "NONE" : this.lastRecipient));
    }

    public void savePlayerData() {
        RedisUtil.publishAsync(RedisUtil.removeGlobalPlayer(this.uuid));

        CorePlugin.getInstance().getPlayerManager().getAllNetworkProfiles().remove(CorePlugin.getInstance().getPlayerManager().getNetworkPlayer(this.originalName));
        CorePlugin.getInstance().getPlayerManager().getAllProfiles().remove(this.uuid);

        this.saveMeta();

        CompletableFuture.runAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", this.uuid), this.getDocument(true), new ReplaceOptions().upsert(true)));
    }

    public void loadPunishmentData() {
        CompletableFuture.runAsync(() -> {
            CorePlugin.getInstance().getPunishmentManager().getPunishments().stream()
                    .filter(punishment -> punishment.getTarget().equals(this.uuid))
                    .forEach(this.punishments::add);

            this.getPunishments().stream()
                    .filter(punishment -> punishment != null && punishment.isActive() && (System.currentTimeMillis() < punishment.getCreatedAt().getTime() + punishment.getPunishmentDuration() || punishment.isPermanent()) && !punishment.isRemoved())
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
                                this.currentlyIpRestricted = true;
                            case BAN:
                                this.currentlyRestricted = true;
                                this.restrictionPunishment = punishment;
                                break;
                        }
                    });
        });
    }

    public void loadPlayerData() {
        this.loadPunishmentData();

        CompletableFuture.supplyAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("_id", this.uuid)).first())
                .thenAcceptAsync(document -> {
                    this.profile = document;

                    if (this.profile == null) {
                        this.saveWithoutRemove();
                        this.hasLoaded = true;
                        return;
                    }

                    if (this.profile.getBoolean("isSynced") != null) {
                        this.setSynced(this.profile.getBoolean("isSynced"));
                    }
                    if (this.profile.getString("syncDiscord") != null) {
                        this.setSyncDiscord(this.profile.getString("syncDiscord"));
                    }
                    if (this.profile.getString("discordSyncCode") != null) {
                        this.setSyncCode(this.profile.getString("discordSyncCode"));
                    } else {
                        this.setSyncCode(SaltUtil.getRandomSaltedString());
                    }
                    if (this.profile.getBoolean("canSeeStaffMessages") != null) {
                        this.canSeeStaffMessages = this.profile.getBoolean("canSeeStaffMessages");
                    }
                    if (this.profile.getBoolean("canSeeTips") != null) {
                        this.canSeeTips = this.profile.getBoolean("canSeeTips");
                    }
                    if (this.profile.getBoolean("canReceiveDms") != null) {
                        this.canReceiveDms = this.profile.getBoolean("canReceiveDms");
                    }
                    if (this.profile.getBoolean("canSeeFiltered") != null) {
                        this.canSeeFiltered = this.profile.getBoolean("canSeeFiltered");
                    }
                    if (this.profile.getBoolean("authBypassed") != null) {
                        this.authBypassed = this.profile.getBoolean("authBypassed");
                    }
                    if (this.profile.getBoolean("canSeeGlobalChat") != null) {
                        this.canSeeGlobalChat = this.profile.getBoolean("canSeeGlobalChat");
                    }
                    if (this.profile.getBoolean("hasVoted") != null) {
                        this.hasVoted = this.profile.getBoolean("hasVoted");
                    }
                    if (this.profile.getLong("lastAuth") != null) {
                        this.lastAuth = this.profile.getLong("lastAuth");
                    }
                    if (this.profile.getBoolean("requiredToAuth") != null) {
                        this.requiredToAuth = this.profile.getBoolean("requiredToAuth");
                    }
                    if (this.profile.getBoolean("autoModMode") != null) {
                        this.isAutoModMode = this.profile.getBoolean("autoModMode");
                    }
                    if (this.profile.getString("previousIpAddress") != null) {
                        this.previousIpAddress = CorePlugin.getInstance().getCryptoManager().decrypt(this.profile.getString("previousIpAddress"));
                    } else {
                        this.previousIpAddress = "NOT_AVAILABLE";
                    }
                    if (this.profile.getString("securityKey") != null) {
                        this.authSecret = this.profile.getString("securityKey");
                    }
                    if (this.profile.getBoolean("autoVanish") != null) {
                        this.isAutoVanish = this.profile.getBoolean("autoVanish");
                    }
                    if (this.profile.getBoolean("canReceiveDmsSounds") != null) {
                        this.canReceiveDmsSounds = this.profile.getBoolean("canReceiveDmsSounds");
                    }
                    if (this.profile.getBoolean("hasSetup2FA") != null) {
                        this.hasSetup2FA = this.profile.getBoolean("hasSetup2FA");
                    }
                    if (this.profile.getString("firstJoined") != null) {
                        this.firstJoin = this.profile.getString("firstJoined");
                    } else {
                        this.firstJoin = CorePlugin.FORMAT.format(new Date());
                    }
                    if (this.profile.getString("language") != null) {
                        this.language = LanguageType.getByName(this.profile.getString("language"));
                    } else {
                        this.language = LanguageType.ENGLISH;
                    }
                    if (this.profile.getInteger("experience") != null) {
                        this.experience = this.profile.getInteger("experience");
                    }
                    if (this.profile.getString("customColor") != null) {
                        this.customColor = ChatColor.valueOf(this.profile.getString("customColor"));
                    }
                    if (this.profile.getString("discord") != null) {
                        this.media.setDiscord(this.profile.getString("discord"));
                    } else {
                        this.media.setDiscord("N/A");
                    }
                    if (this.profile.getString("twitter") != null) {
                        this.media.setTwitter(this.profile.getString("twitter"));
                    } else {
                        this.media.setTwitter("N/A");
                    }
                    if (this.profile.getString("youtube") != null) {
                        this.media.setYoutubeLink(this.profile.getString("youtube"));
                    } else {
                        this.media.setYoutubeLink("N/A");
                    }
                    if (this.profile.getString("instagram") != null) {
                        this.media.setInstagram(this.profile.getString("instagram"));
                    } else {
                        this.media.setInstagram("N/A");
                    }
                    if (this.profile.getList("allGrants", String.class).isEmpty()) {
                        this.allGrants.add(this.getDefaultGrant());
                    } else {
                        final List<String> allGrants = this.profile.getList("allGrants", String.class);
                        allGrants.forEach(s -> this.allGrants.add(CorePlugin.GSON.fromJson(s, Grant.class)));
                    }

                    final String appliedPrefix = this.profile.getString("appliedPrefix");

                    if (appliedPrefix != null && !appliedPrefix.equals("Default")) {
                        this.appliedPrefix = Prefix.getByName(appliedPrefix);
                    }

                    if (this.profile.get("allIgnored") != null) {
                        final List<String> ignoring = this.profile.getList("allIgnored", String.class);

                        if (!ignoring.isEmpty()) {
                            this.allIgnoring.addAll(ignoring);
                        }
                    }

                    if (this.profile.get("allPermissions") != null) {
                        final List<String> permissions = this.profile.getList("allPermissions", String.class);

                        if (!permissions.isEmpty()) {
                            this.userPermissions.addAll(permissions);
                        }
                    }

                    if (this.profile.get("metaData") != null) {
                        final Map<?, ?> serializedMetaData = this.profile.get("metaData", Map.class);

                        serializedMetaData.forEach((k, v) -> this.metaDataEntryMap.put((String) k, CorePlugin.GSON.fromJson((String) v, MetaDataEntry.class)));
                    }

                    this.loadMetaData();

                    this.currentlyOnline = true;
                    this.hasLoaded = true;
                });


        Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> RedisUtil.publishAsync(RedisUtil.addGlobalPlayer(this)), 10L);
        Bukkit.getScheduler().runTaskLaterAsynchronously(CorePlugin.getInstance(), this::saveWithoutRemove, 60L);
    }

    private void loadMetaData() {
        if (this.getMetaData("chat-channel") != null && !this.getMetaData("chat-channel").getValue().getAsString().equals("NONE")) {
            this.setChannel(ChatChannelType.valueOf(this.getMetaData("chat-channel").getValue().getAsString()));
        }

        if (this.getMetaData("last-messenger") != null && !this.getMetaData("last-messenger").getValue().getAsString().equals("NONE")) {
            this.lastRecipient = this.getMetaData("last-messenger").getValue().getAsString();
        }
    }

    public boolean findIpRelative(AsyncPlayerPreLoginEvent loginEvent, boolean hub) {
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        if (this.currentlyBlacklisted) {
            return false;
        }

        CompletableFuture.supplyAsync(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("previousIpAddress", this.encryptedIpAddress)).iterator())
                .thenAcceptAsync(documentIterator -> {
                    while (documentIterator.hasNext() && !atomicBoolean.get()) {
                        final Document document = documentIterator.next();

                        if (document.getBoolean("blacklisted") != null && document.getBoolean("blacklisted") && !document.getString("name").equalsIgnoreCase(loginEvent.getName())) {
                            this.currentlyRestricted = true;
                            this.currentlyBlacklisted = true;
                            this.relatedToBlacklist = true;

                            final Rank rank = Rank.getByName(document.getString("rank"));
                            final String fancyFormat = (rank != null ? Color.translate(rank.getColor()) : ChatColor.GRAY) + document.getString("name");

                            this.relatedTo = fancyFormat;

                            if (!hub) {
                                loginEvent.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Color.translate(PunishmentStrings.BLACK_LIST_RELATION_MESSAGE.replace("<player>", fancyFormat)));
                            }

                            atomicBoolean.set(true);
                        }

                        if (document.getBoolean("restricted") != null && document.getBoolean("restricted") && !document.getString("name").equalsIgnoreCase(loginEvent.getName())) {
                            this.currentlyRestricted = true;
                            this.currentlyIpRestricted = true;
                            this.relatedToIpBanned = true;

                            final Rank rank = Rank.getByName(document.getString("rank"));
                            final String fancyFormat = (rank != null ? Color.translate(rank.getColor()) : ChatColor.GRAY) + document.getString("name");

                            this.relatedToIpBannedWho = fancyFormat;

                            if (!hub) {
                                loginEvent.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Color.translate(PunishmentStrings.IP_BAN_RELATION_MESSAGE.replace("<player>", fancyFormat)));
                            }

                            atomicBoolean.set(true);
                        }
                    }
                });

        return atomicBoolean.get();
    }

    public void onAfterDataLoad() {
        this.player = Bukkit.getPlayer(this.uuid);
        this.attachment = this.player.addAttachment(CorePlugin.getInstance());
        this.gameProfile = CorePlugin.getInstance().getPlayerManager().getGameProfile(this.player);
        this.rainbowNametag = new RainbowNametag(this.player, CorePlugin.getInstance());

        CompletableFuture.runAsync(() -> {
            this.setupPlayer();

            if (CorePlugin.getInstance().getServerSettings().isNameMcEnabled()) {
                this.checkVoting();
            }

            final Property property = this.gameProfile.getProperties().get("textures").stream().findFirst().orElse(null);

            if (property != null) {
                this.skin = property.getValue();
                this.signature = property.getSignature();
            }
        });

        if (this.player.hasPermission("scandium.staff") && !CorePlugin.getInstance().getPlayerManager().isOnline(this.player.getName()) && !CorePlugin.getInstance().getServerSettings().isUsingXenon()) {
            Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> RedisUtil.publishAsync(RedisUtil.onConnect(this.player)), 30L);
        }

        if (this.profile == null) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            if (this.profile.getList("allPrefixes", String.class) != null) {
                if (this.player.hasPermission("scandium.prefixes.all")) {
                    final List<String> prefixes = new ArrayList<>();

                    Prefix.getPrefixes().forEach(prefix -> prefixes.add(prefix.getName()));

                    this.allPrefixes.addAll(prefixes);
                } else {
                    final List<String> prefixes = this.profile.getList("allPrefixes", String.class);

                    this.allPrefixes.addAll(prefixes);
                }
            }
        });

        Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> {
            if (this.profile.getBoolean("currentlyDisguised") != null) {
                if (this.profile.getBoolean("currentlyDisguised")) {
                    final DisguiseData disguiseData = CorePlugin.getInstance().getDisguiseCache().getRandomData();
                    final DisguiseData skinData = CorePlugin.getInstance().getDisguiseCache().getRandomData();

                    if (disguiseData != null && skinData != null) {
                        Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> CorePlugin.getInstance().getDisguiseManager().disguise(this.player, disguiseData, skinData, (this.disguiseRank == null ? Rank.getDefault() : this.disguiseRank)), 15L);
                    } else {
                        this.player.sendMessage(ChatColor.RED + "We couldn't disguise you as there aren't any available disguises!");
                    }
                }
            }
        });
    }

    public String getWarningMessage() {
        return ChatColor.RED + "You currently have an active warning for " + this.warningPunishment.getReason() + ".\n" + ChatColor.RED + "This warning will expire in " + ChatColor.YELLOW + this.warningPunishment.getDurationString() + ChatColor.RED + ".";
    }

    public String getRestrictionMessage() {
        if (this.relatedToIpBanned) {
            return Color.translate(PunishmentStrings.IP_BAN_RELATION_MESSAGE.replace("<player>", this.relatedToIpBannedWho));
        }

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
                .filter(grant -> grant != null && grant.getRank() != null && !grant.isRemoved() && grant.isActive() && !grant.getRank().isHidden() && (grant.getScope() == null || grant.isGlobal() || grant.isApplicable()))
                .findFirst().orElseGet(this::getDefaultGrant);
    }

    public Grant getDefaultGrant() {
        return new Grant(null, Rank.getDefault(), System.currentTimeMillis(), -1L, "Automatic Grant (Default)", true, true);
    }

    public void setupPlayer() {
        CompletableFuture.runAsync(() -> {
            this.resetPermissions();
            this.setupPermissions();
        });

        Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), () -> {
            this.setupPlayerTag();
            this.setupPlayerList();
        }, 20L);
    }

    public void setupPlayerList() {
        final String listFormatted = Color.translate(
                (this.disguiseRank != null ? this.disguiseRank.getColor() : this.getActiveGrant().getRank().getColor()) + this.getName()
        );

        this.player.setPlayerListName(listFormatted);
        this.player.setDisplayName(listFormatted);

        if (CorePlugin.getInstance().getServerSettings().isTabEnabled()) {
            CorePlugin.getInstance().getNMS().updateTablist();
        }
    }

    public void resetPermissions() {
        if (!this.attachment.getPermissions().isEmpty()) {
            final Set<String> keySet = new HashSet<>(this.attachment.getPermissions().keySet());

            keySet.forEach(s -> this.attachment.unsetPermission(s));
        }
    }

    public void setupPermissions() {
        final Consumer<? super String> action = (Consumer<String>) s -> {
            if (s.startsWith("b-")) {
                this.bungeePermissions.add(s.replace("b-", ""));
                return;
            }

            if (s.equals("scandium.staff") && !this.bungeePermissions.contains("scandium.staff")) {
                this.bungeePermissions.add(s);
            }

            this.attachment.setPermission(s.replace("*", ""), !s.startsWith("*"));
        };

        CompletableFuture.runAsync(() -> {
            this.getAllGrants().stream()
                    .filter(grant -> grant != null && grant.isActive() && (grant.isApplicable() || grant.isGlobal()))
                    .forEach(grant -> {
                        grant.getRank().getPermissions().forEach(action);
                        grant.getRank().getInheritance().stream().map(Rank::getByUuid).filter(Objects::nonNull).forEach(rank -> rank.getPermissions().forEach(action));
                    });
            this.getUserPermissions().forEach(s -> this.attachment.setPermission(s.replace("*", ""), !s.startsWith("*")));
        });

        this.player.recalculatePermissions();

        Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> CorePlugin.getInstance().getServerManager().syncPermissions(this.player, this.getColorByRankColor() + this.player.getName(), this.bungeePermissions));
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

    public ChatColor getOriginalRankColor() {
        return ChatColor.getByChar(this.getActiveGrant().getRank().getColor().replace("&", "").replace("§", ""));
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
                    this.allPrefixes.add("Liked");

                    if (this.appliedPrefix == null) {
                        this.appliedPrefix = Prefix.getByName("Liked");
                    }
                    if (this.player != null) {
                        this.player.sendMessage(new String[]{
                                Color.SECONDARY_COLOR + "Thanks for voting for our server on " + ChatColor.AQUA + "NameMC" + Color.SECONDARY_COLOR + "!",
                                Color.SECONDARY_COLOR + "You've been granted the " + ChatColor.AQUA + "✔ " + ChatColor.GRAY + "(Liked)" + Color.SECONDARY_COLOR + " prefix!"
                        });
                    }
                }
            } else {
                if (!VotingUtil.hasVoted(this.uuid.toString())) {
                    this.hasVoted = false;
                    this.allPrefixes.remove("Liked");

                    this.player.sendMessage(ChatColor.RED + Color.translate("Your permission to access the &b✔ &7(Liked) &ctag has been revoked as You've unliked our server on NameMC!"));
                    this.player.sendMessage(ChatColor.RED + Color.translate("To gain your tag back, like us on NameMC again!"));
                }
            }
        });
    }

    public boolean hasMetaData(String key) {
        return this.metaDataEntryMap.containsKey(key);
    }

    public MetaDataEntry getMetaData(String key) {
        return this.metaDataEntryMap.getOrDefault(key, null);
    }

    public void addMetaData(String key, MetaDataValue value) {
        this.metaDataEntryMap.put(key, new MetaDataEntry(value));
    }

    public void setMetaData(String key, MetaDataEntry value) {
        if (this.hasMetaData(key)) {
            this.metaDataEntryMap.replace(key, value);
        } else {
            this.metaDataEntryMap.put(key, value);
        }
    }

    public void setMetaData(String key, MetaDataValue value) {
        if (this.hasMetaData(key)) {
            this.metaDataEntryMap.replace(key, new MetaDataEntry(value));
        } else {
            this.metaDataEntryMap.put(key, new MetaDataEntry(value));
        }
    }

    public void deleteMetaData(String key) {
        this.metaDataEntryMap.remove(key);
    }
}
