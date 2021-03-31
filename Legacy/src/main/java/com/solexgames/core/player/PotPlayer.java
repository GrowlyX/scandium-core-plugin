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
import com.solexgames.core.player.hook.AchievementData;
import com.solexgames.core.player.prefixes.Prefix;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentStrings;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.enums.PotionMessageType;
import com.solexgames.core.player.global.NetworkPlayer;
import com.solexgames.core.util.Color;
import com.solexgames.core.util.RedisUtil;
import com.solexgames.core.util.SaltUtil;
import com.solexgames.core.util.external.NameMCExternal;
import com.solexgames.core.util.external.NameTagExternal;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
    private List<PotionMessageType> allPurchasedMessages = new ArrayList<>();

    @SerializedName("_id")
    private UUID uuid;
    private Player player;
    private String ipAddress;
    private Media media;
    private Prefix appliedPrefix;

    private Player lastRecipient;

    private ChatColor customColor;

    private String rankName;
    private String syncCode;
    private String syncDiscord;
    private String name;

    private Document profile;

    private boolean canSeeStaffMessages = true;
    private boolean canSeeGlobalChat = true;
    private boolean canReceiveDms = true;
    private boolean canReceiveDmsSounds = true;
    private boolean canSeeBroadcasts = true;
    private boolean canSeeFiltered = true;
    private boolean canSeeTips = true;
    private boolean canAcceptingFriendRequests = true;

    private ItemStack[] itemHistory;
    private ItemStack[] armorHistory;

    private boolean canReport = true;
    private boolean canRequest = true;
    private boolean hasVoted = false;
    private boolean isVanished = false;
    private boolean isStaffMode = false;
    private boolean isFrozen = false;
    private boolean isSynced = false;
    private boolean isLoaded = false;

    private boolean isAutoVanish = false;
    private boolean isAutoModMode = false;

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

    private boolean isReasonEditing = false;
    private PunishmentType reasonType = null;
    private String reasonTarget = null;

    private boolean currentlyMuted;
    private boolean currentlyRestricted;
    private boolean currentlyOnline;

    private LanguageType language;
    private PermissionAttachment attachment;
    private GameProfile gameProfile;

    private long chatCooldown = 1L;
    private long commandCooldown = 1L;

    private Date lastJoined;

    private String lastJoin;
    private String firstJoin;

    private Punishment restrictionPunishment;
    private AchievementData achievementData;
    private PotionMessageType potionMessageType;

    private Rank disguiseRank;

    private int experience;

    private boolean hasLoaded;

    public PotPlayer(UUID uuid, String name, InetAddress inetAddress) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
        this.ipAddress = inetAddress.toString();
        this.name = name;

        this.attachment = this.player.addAttachment(CorePlugin.getPlugin(CorePlugin.class));

        this.media = new Media();
        this.lastJoined = new Date();
        this.syncCode = SaltUtil.getRandomSaltedString(6);
        this.hasLoaded = false;

        CorePlugin.getInstance().getPlayerManager().getAllProfiles().put(uuid, this);

        this.loadPlayerData();
    }

    public void saveWithoutRemove() {
        Document document = new Document("_id", this.uuid);

        document.put("name", this.getName());
        document.put("uuid", this.uuid.toString());
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

        List<String> grantStrings = new ArrayList<>();
        this.getAllGrants().forEach(grant -> grantStrings.add(grant.toJson()));

        List<String> messages = new ArrayList<>();
        this.getAllPurchasedMessages().forEach(grant -> messages.add(grant.typeName));

        List<String> prefixStrings = new ArrayList<>(this.getAllPrefixes());

        document.put("allGrants", grantStrings);
        document.put("allMessages", messages);
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
        document.put("currentlyOnline", this.currentlyOnline);

        document.put("discord", this.media.getDiscord());
        document.put("twitter", this.media.getTwitter());
        document.put("instagram", this.media.getInstagram());
        document.put("youtube", this.media.getYoutubeLink());

        document.put("achievementData", CorePlugin.GSON.toJson(this.achievementData));
        if (this.potionMessageType != null) {
            document.put("potionMessageType", this.potionMessageType.getTypeName());
        } else {
            document.put("potionMessageType", "NORMAL");
        }

        document.put("autoVanish", this.isAutoVanish);
        document.put("autoModMode", this.isAutoModMode);
        document.put("ipAddress", CorePlugin.getInstance().getCryptoManager().encrypt(this.ipAddress));
        document.put("experience", this.experience);

        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", uuid), document, new ReplaceOptions().upsert(true)));
    }

    public void savePlayerData() {
        CorePlugin.getInstance().getPlayerManager().getAllNetworkProfiles().remove(this.uuid);
        RedisUtil.writeAsync(RedisUtil.removeGlobalPlayer(this.uuid));

        Document document = new Document("_id", this.uuid);

        document.put("name", this.getName());
        document.put("uuid", this.uuid.toString());
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

        List<String> grantStrings = new ArrayList<>();
        this.getAllGrants().forEach(grant -> grantStrings.add(grant.toJson()));

        List<String> messages = new ArrayList<>();
        this.getAllPurchasedMessages().forEach(grant -> messages.add(grant.typeName));

        List<String> prefixStrings = new ArrayList<>(this.getAllPrefixes());

        document.put("allGrants", grantStrings);
        document.put("allMessages", messages);
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
        document.put("currentlyOnline", false);

        document.put("discord", this.media.getDiscord());
        document.put("twitter", this.media.getTwitter());
        document.put("instagram", this.media.getInstagram());
        document.put("youtube", this.media.getYoutubeLink());

        document.put("achievementData", CorePlugin.GSON.toJson(this.achievementData));
        if (this.potionMessageType != null) {
            document.put("potionMessageType", this.potionMessageType.getTypeName());
        } else {
            document.put("potionMessageType", "NORMAL");
        }

        document.put("autoVanish", this.isAutoVanish);
        document.put("autoModMode", this.isAutoModMode);
        document.put("ipAddress", CorePlugin.getInstance().getCryptoManager().encrypt(ipAddress));
        document.put("experience", this.experience);

        CorePlugin.getInstance().getMongoThread().execute(() ->
                CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", uuid), document, new ReplaceOptions().upsert(true))
        );

        CorePlugin.getInstance().getPlayerManager().getAllProfiles().remove(this.uuid);
    }

    public CompletableFuture<Document> fetchDocument() {
        CompletableFuture<Document> completableFuture = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            Document document = CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("_id", uuid)).first();
            this.setProfile(document);
            completableFuture.complete(document);
        });

        return completableFuture;
    }

    public void loadPlayerData() {
        this.fetchDocument().thenRun(() -> {
            if (this.profile == null) {
                this.saveWithoutRemove();
                this.hasLoaded = true;

                System.out.println("[Debug] A document was null.");

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
            if (profile.getBoolean("autoVanish") != null) {
                this.isAutoVanish = profile.getBoolean("autoVanish");
            }
            if (profile.getBoolean("canReceiveDmsSounds") != null) {
                this.canReceiveDmsSounds = profile.getBoolean("canReceiveDmsSounds");
            }
            if (profile.getString("firstJoined") != null) {
                this.firstJoin = profile.getString("firstJoined");
            } else {
                this.firstJoin = CorePlugin.FORMAT.format(new Date());
            }
            if (profile.getString("disguiseRank") != null) {
                Rank rank = Rank.getByName(profile.getString("disguiseRank"));

                if (rank != null) {
                    this.disguiseRank = rank;
                }
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
            if (profile.getString("potionMessageType") != null) {
                this.potionMessageType = PotionMessageType.valueOf(profile.getString("potionMessageType"));
            } else {
                this.potionMessageType = PotionMessageType.NORMAL;
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

            if ((((List<String>) profile.get("allGrants")).isEmpty()) || (profile.get("allGrants") == null)) {
                this.allGrants.add(new Grant(null, Objects.requireNonNull(Rank.getDefault()), new Date().getTime(), -1L, "Automatic Grant (Default)", true, true));
            } else {
                List<String> allGrants = ((List<String>) profile.get("allGrants"));
                allGrants.forEach(s -> this.allGrants.add(CorePlugin.GSON.fromJson(s, Grant.class)));
            }

            if ((profile.getString("appliedPrefix") != null) && !profile.getString("appliedPrefix").equals("Default")) {
                this.appliedPrefix = Prefix.getByName(profile.getString("appliedPrefix"));
            } else {
                this.appliedPrefix = null;
            }

            if (profile.get("allIgnored") != null) {
                List<String> ignoring = ((List<String>) profile.get("allIgnored"));
                if (!ignoring.isEmpty()) {
                    this.allIgnoring.addAll(ignoring);
                }
            }
            if (profile.get("allPermissions") != null) {
                List<String> permissions = ((List<String>) profile.get("allPermissions"));
                if (!permissions.isEmpty()) {
                    this.userPermissions.addAll(permissions);
                }
            }
            if (profile.get("allMessages") != null) {
                List<String> allMessages = ((List<String>) profile.get("allMessages"));
                if (!allMessages.isEmpty()) {
                    allMessages.forEach(s -> this.allPurchasedMessages.add(PotionMessageType.valueOf(s)));
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

            CorePlugin.getInstance().getPunishmentManager().getPunishments()
                    .stream()
                    .filter(punishment -> punishment.getTarget().equals(this.uuid))
                    .forEach(punishment -> this.punishments.add(punishment));

            CorePlugin.getInstance().getPlayerManager().getAllSyncCodes().put(this.syncCode, this.getName());

            this.getPunishments()
                    .stream()
                    .filter(punishment -> punishment.getPunishmentType().equals(PunishmentType.MUTE))
                    .filter(Punishment::isActive)
                    .filter(punishment -> !punishment.isRemoved())
                    .forEach(punishment -> this.currentlyMuted = true);

            this.getPunishments()
                    .stream()
                    .filter(punishment -> punishment.getPunishmentType().equals(PunishmentType.BAN) || punishment.getPunishmentType().equals(PunishmentType.BLACKLIST) || punishment.getPunishmentType().equals(PunishmentType.IPBAN))
                    .filter(Punishment::isActive)
                    .filter(punishment -> !punishment.isRemoved())
                    .forEach(punishment -> {
                        this.currentlyRestricted = true;
                        this.restrictionPunishment = punishment;
                    });

            this.currentlyOnline = true;
            this.hasLoaded = true;

            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    new NetworkPlayer(uuid, name, CorePlugin.getInstance().getServerName(), getActiveGrant().getRank().getName(), isCanReceiveDms(), ipAddress, syncCode, isSynced);
                }
            });

            Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), this::saveWithoutRemove, 10 * 20L);
            RedisUtil.writeAsync(RedisUtil.addGlobalPlayer(this));

            this.gameProfile = CorePlugin.getInstance().getPlayerManager().getGameProfile(this.player);

            if (this.getPlayer().hasPermission("scandium.staff")) {
                if (!CorePlugin.getInstance().getPlayerManager().isOnline(this.getPlayer().getName())) {
                    RedisUtil.writeAsync(RedisUtil.onConnect(this.getPlayer()));
                }
            }

            if (profile != null && profile.get("allPrefixes") != null) {
                if (player.hasPermission("scandium.prefixes.all")) {
                    List<String> prefixes = new ArrayList<>();
                    Prefix.getPrefixes().forEach(prefix -> prefixes.add(prefix.getName()));
                    this.getAllPrefixes().addAll(prefixes);
                } else if (!player.hasPermission("scandium.prefixes.all")) {
                    List<String> prefixes = ((List<String>) this.profile.get("allPrefixes"));
                    this.allPrefixes.addAll(prefixes);
                }
            }

            if (CorePlugin.NAME_MC_REWARDS) {
                this.checkVoting();
            }

            this.setupPlayer();
        });
    }

    public String getRestrictionMessage() {
        switch (this.restrictionPunishment.getPunishmentType()) {
            case BLACKLIST:
                return Color.translate(PunishmentStrings.BLACK_LIST_MESSAGE.replace("<reason>", this.restrictionPunishment.getReason()));
            case IPBAN:
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
                .filter(Objects::nonNull)
                .filter(grant -> grant.isActive() && !grant.getRank().isHidden() && (grant.getScope() == null || grant.isGlobal() || grant.isApplicable()))
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
        this.setupPlayerTag();
        this.setupPlayerList();
    }

    public void setupDisplay() {
        Grant grant = this.getActiveGrant();

        if (grant == null) {
            Grant newGrant = this.getDefaultGrant();

            this.getAllGrants().add(newGrant);
            this.player.setDisplayName(Color.translate(newGrant.getRank().getColor() + player.getName()));

            return;
        }

        this.player.setDisplayName(Color.translate(grant.getRank().getColor()) + player.getName());
    }

    public void setupPlayerList() {
        player.setPlayerListName(Color.translate((this.getActiveGrant().getRank().getColor() == null ? ChatColor.GRAY.toString() : this.getActiveGrant().getRank().getColor()) + (this.customColor != null ? this.customColor : "") + this.player.getName()));

        CorePlugin.getInstance().getNMS().updateTablist();
    }

    public void resetPermissions() {
        if (!this.attachment.getPermissions().isEmpty()) {
            this.attachment.getPermissions().keySet().forEach(s -> this.attachment.unsetPermission(s));
        }
    }

    public void setupPermissions() {
        this.getAllGrants().stream()
                .filter(grant -> grant != null && grant.isActive() && (grant.isApplicable() || grant.isGlobal()))
                .forEach(grant -> {
                    grant.getRank().getPermissions().forEach(s -> this.attachment.setPermission(s.replace("*", ""), !s.startsWith("*")));
                    grant.getRank().getInheritance().stream().map(Rank::getByUuid).filter(Objects::nonNull).forEach(rank -> rank.getPermissions().forEach(s -> this.attachment.setPermission(s.replace("*", ""), !s.startsWith("*"))));
                });
        this.getUserPermissions().forEach(s -> this.attachment.setPermission(s.replace("*", ""), !s.startsWith("*")));

        this.player.recalculatePermissions();
    }

    public void setupPlayerTag() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (this.getActiveGrant().getRank().getColor() != null) {
                if (this.isStaffMode()) {
                    if (player.hasPermission("scandium.staff")) {
                        NameTagExternal.setupStaffModeTag(player, this.player);
                    } else {
                        NameTagExternal.setupNameTag(player, this.player, this.getColorByRankColor());
                    }
                } else if (this.isVanished()) {
                    if (player.hasPermission("scandium.staff")) {
                        NameTagExternal.setupVanishTag(player, this.player);
                    } else {
                        NameTagExternal.setupNameTag(player, this.player, this.getColorByRankColor());
                    }
                } else {
                    NameTagExternal.setupNameTag(player, this.player, this.getColorByRankColor());
                }
            } else {
                NameTagExternal.setupNameTag(player, this.player, ChatColor.GRAY);
            }
        });
    }

    public ChatColor getColorByRankColor() {
        if (this.disguiseRank != null) {
            return ChatColor.getByChar(this.getDisguiseRank().getColor().replace("&", "").replace("§", ""));
        }

        if (this.getActiveGrant().getRank().getColor() != null) {
            return ChatColor.getByChar(this.getActiveGrant().getRank().getColor().replace("&", "").replace("§", ""));
        } else {
            return ChatColor.GRAY;
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
        if (!hasVoted) {
            if (NameMCExternal.hasVoted(this.uuid.toString())) {
                this.hasVoted = true;
                this.getAllPrefixes().add("Liked");

                if (this.getAppliedPrefix() == null) this.appliedPrefix = Prefix.getByName("Liked");
                if (player != null) {
                    player.sendMessage(Color.translate("&aThanks for voting for us on &6NameMC&a!"));
                    player.sendMessage(Color.translate("&aYou have been granted the &b✔ &7(Liked)&a prefix!"));
                }
            }
        } else {
            if (!NameMCExternal.hasVoted(this.uuid.toString())) {
                this.hasVoted = false;
                this.getAllPrefixes().remove("Liked");

                player.sendMessage(Color.translate("&cYour permission to access the &b✔ &7(Liked) &ctag has been revoked as you have unliked our server on NameMC!"));
                player.sendMessage(Color.translate("&cTo gain your tag back, like us on NameMC again!"));
            }
        }
    }
}
