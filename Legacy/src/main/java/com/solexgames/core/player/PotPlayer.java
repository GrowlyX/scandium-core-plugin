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

    private Document document;

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

    private int experience;

    private boolean hasLoaded;

    public PotPlayer(UUID uuid, String name, InetAddress inetAddress) {
        this.uuid = uuid;
        this.ipAddress = inetAddress.toString();
        this.name = name;

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

        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", uuid), document, new ReplaceOptions().upsert(true)));

        CorePlugin.getInstance().getPlayerManager().getAllSyncCodes().remove(this.syncCode);
        CorePlugin.getInstance().getPlayerManager().getAllProfiles().remove(this.uuid);
    }

    public void loadPlayerData() {
        Document document = CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("_id", this.uuid)).first();
        if (document == null) {
            this.saveWithoutRemove();
            return;
        }

        this.name = this.getName();
        this.document = document;

        if (document.getBoolean("canSeeStaffMessages") != null) {
            this.canSeeStaffMessages = document.getBoolean("canSeeStaffMessages");
        }
        if (document.getBoolean("canSeeTips") != null) {
            this.canSeeTips = document.getBoolean("canSeeTips");
        }
        if (document.getBoolean("canReceiveDms") != null) {
            this.canReceiveDms = document.getBoolean("canReceiveDms");
        }
        if (document.getBoolean("canSeeFiltered") != null) {
            this.canSeeFiltered = document.getBoolean("canSeeFiltered");
        }
        if (document.getBoolean("canSeeBroadcasts") != null) {
            this.canSeeBroadcasts = document.getBoolean("canSeeBroadcasts");
        }
        if (document.getBoolean("canSeeGlobalChat") != null) {
            this.canSeeGlobalChat = document.getBoolean("canSeeGlobalChat");
        }
        if (document.getBoolean("hasVoted") != null) {
            this.hasVoted = document.getBoolean("hasVoted");
        }
        if (document.getBoolean("autoModMode") != null) {
            this.isAutoModMode = document.getBoolean("autoModMode");
        }
        if (document.getBoolean("autoVanish") != null) {
            this.isAutoVanish = document.getBoolean("autoVanish");
        }
        if (document.getBoolean("canReceiveDmsSounds") != null) {
            this.canReceiveDmsSounds = document.getBoolean("canReceiveDmsSounds");
        }
        if (document.getString("firstJoined") != null) {
            this.firstJoin = document.getString("firstJoined");
        } else {
            this.firstJoin = CorePlugin.FORMAT.format(new Date());
        }
        if (document.getString("language") != null) {
            this.language = LanguageType.getByName(document.getString("language"));
        } else {
            this.language = LanguageType.ENGLISH;
        }
        if (document.getInteger("experience") != null) {
            this.experience = document.getInteger("experience");
        } else {
            this.experience = 0;
        }
        if (document.getString("customColor") != null) {
            this.customColor = ChatColor.valueOf(document.getString("customColor"));
        }
        if (document.getString("discord") != null) {
            this.media.setDiscord(document.getString("discord"));
        } else {
            this.media.setDiscord("N/A");
        }
        if (document.getString("potionMessageType") != null) {
            this.potionMessageType = PotionMessageType.valueOf(document.getString("potionMessageType"));
        } else {
            this.potionMessageType = PotionMessageType.NORMAL;
        }
        if (document.getString("twitter") != null) {
            this.media.setTwitter(document.getString("twitter"));
        } else {
            this.media.setTwitter("N/A");
        }
        if (document.getString("youtube") != null) {
            this.media.setYoutubeLink(document.getString("youtube"));
        } else {
            this.media.setYoutubeLink("N/A");
        }
        if (document.getString("instagram") != null) {
            this.media.setInstagram(document.getString("instagram"));
        } else {
            this.media.setInstagram("N/A");
        }

        if ((((List<String>) document.get("allGrants")).isEmpty()) || (document.get("allGrants") == null)) {
            this.allGrants.add(new Grant(null, Objects.requireNonNull(Rank.getDefault()), new Date().getTime(), -1L, "Automatic Grant (Default)", true, true));
        } else {
            List<String> allGrants = ((List<String>) document.get("allGrants"));
            allGrants.forEach(s -> this.allGrants.add(CorePlugin.GSON.fromJson(s, Grant.class)));
        }

        if ((document.getString("appliedPrefix") != null) && !document.getString("appliedPrefix").equals("Default")) {
            this.appliedPrefix = Prefix.getByName(document.getString("appliedPrefix"));
        } else {
            this.appliedPrefix = null;
        }

        if (document.get("allIgnored") != null) {
            List<String> ignoring = ((List<String>) document.get("allIgnored"));
            if (!ignoring.isEmpty()) {
                this.allIgnoring.addAll(ignoring);
            }
        }
        if (document.get("allPermissions") != null) {
            List<String> permissions = ((List<String>) document.get("allPermissions"));
            if (!permissions.isEmpty()) {
                this.userPermissions.addAll(permissions);
            }
        }
        if (document.get("allMessages") != null) {
            List<String> allMessages = ((List<String>) document.get("allMessages"));
            if (!allMessages.isEmpty()) {
                allMessages.forEach(s -> this.allPurchasedMessages.add(PotionMessageType.valueOf(s)));
            }
        }

        if (document.getBoolean("isSynced") != null) {
            this.setSynced(document.getBoolean("isSynced"));
        }
        if (document.getString("syncDiscord") != null) {
            this.setSyncDiscord(document.getString("syncDiscord"));
        }
        if (document.getString("discordSyncCode") != null) {
            this.setSyncCode(document.getString("discordSyncCode"));
        } else {
            this.setSyncCode(SaltUtil.getRandomSaltedString());
        }
        if (document.getString("achievementData") != null) {
            this.achievementData = CorePlugin.GSON.fromJson(document.getString("achievementData"), AchievementData.class);
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

        new NetworkPlayer(this.uuid, this.name, CorePlugin.getInstance().getServerName(), this.getActiveGrant().getRank().getName(), this.isCanReceiveDms(), this.ipAddress);

        Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), this::saveWithoutRemove, 10 * 20L);
        RedisUtil.writeAsync(RedisUtil.addGlobalPlayer(this));
    }

    public void postLoginLoad() {
        this.player = Bukkit.getPlayer(uuid);
        this.gameProfile = CorePlugin.getInstance().getPlayerManager().getGameProfile(this.player);

        if (this.getPlayer().hasPermission("scandium.staff")) {
            if (!CorePlugin.getInstance().getPlayerManager().isOnline(this.getPlayer().getName())) {
                CorePlugin.getInstance().getRedisThread().execute(() -> CorePlugin.getInstance().getRedisManager().write(RedisUtil.onConnect(this.getPlayer())));
            }
        }

        if (document.get("allPrefixes") != null) {
            if (player.hasPermission("scandium.prefixes.all")) {
                List<String> prefixes = new ArrayList<>();
                Prefix.getPrefixes().forEach(prefix -> prefixes.add(prefix.getName()));
                this.getAllPrefixes().addAll(prefixes);
            } else if (!player.hasPermission("scandium.prefixes.all")) {
                List<String> prefixes = ((List<String>) this.document.get("allPrefixes"));
                this.allPrefixes.addAll(prefixes);
            }
        }

        if (CorePlugin.NAME_MC_REWARDS) {
            this.checkVoting();
        }

        this.setupPlayer();
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
        this.attachment = this.player.addAttachment(CorePlugin.getPlugin(CorePlugin.class));
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
            try {
                if (NameMCExternal.hasVoted(this.uuid.toString())) {
                    this.hasVoted = true;
                    this.getAllPrefixes().add("Liked");

                    if (this.getAppliedPrefix() == null) this.appliedPrefix = Prefix.getByName("Liked");
                    if (player != null) {
                        player.sendMessage(Color.translate("&aThanks for voting for us on &6NameMC&a!"));
                        player.sendMessage(Color.translate("&aYou have received the &b✔ &7(Liked)&a prefix!"));
                    }
                }
            } catch (Exception exception) {
                CorePlugin.getInstance().getLogger().warning("[NameMC] Could not check " + player.getName() + "'s voting status!");
                CorePlugin.getInstance().getLogger().warning("[NameMC] Is your server on NameMC? Exception: " + exception.getMessage());
            }
        } else {
            try {
                if (!NameMCExternal.hasVoted(this.uuid.toString())) {
                    this.hasVoted = false;
                    this.getAllPrefixes().remove("Liked");

                    player.sendMessage(Color.translate("&cYour &b✔ &7(Liked) &ctag has been revoked as you have unliked our server on NameMC!"));
                    player.sendMessage(Color.translate("&cTo gain your tag back, like us on NameMC again!"));
                }
            } catch (Exception exception) {
                CorePlugin.getInstance().getLogger().warning("[NameMC] Could not check " + player.getName() + "'s voting status!");
                CorePlugin.getInstance().getLogger().warning("[NameMC] Is your server on NameMC? Exception: " + exception.getMessage());
            }
        }
    }
}
