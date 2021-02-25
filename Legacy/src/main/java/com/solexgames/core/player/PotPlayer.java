package com.solexgames.core.player;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.solexgames.core.CorePlugin;
import com.solexgames.core.board.ScoreBoard;
import com.solexgames.core.enums.LanguageType;
import com.solexgames.core.media.Media;
import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.player.hook.AchievementData;
import com.solexgames.core.player.prefixes.Prefix;
import com.solexgames.core.player.punishment.Punishment;
import com.solexgames.core.player.punishment.PunishmentType;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.potion.PotionMessageType;
import com.solexgames.core.util.Color;
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

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class PotPlayer {

    @Getter
    public static Map<UUID, PotPlayer> profilePlayers = new HashMap<>();
    @Getter
    public static Map<String, String> syncCodes = new HashMap<>();

    private List<Punishment> punishments = new ArrayList<>();
    private List<Grant> allGrants = new ArrayList<>();
    private List<String> allPrefixes = new ArrayList<>();
    private List<String> allIgnoring = new ArrayList<>();
    private List<String> allFriends = new ArrayList<>();
    private List<String> userPermissions = new ArrayList<>();
    private List<PotionMessageType> allPurchasedMessages = new ArrayList<>();

    private UUID uuid;
    private Player player;
    private Media media;
    private Prefix appliedPrefix;

    private Player lastRecipient;

    private ChatColor customColor;

    private String rankName;
    private String syncCode;
    private String syncDiscord;
    private String name;

    private boolean canSeeStaffMessages = true;
    private boolean canSeeGlobalChat = true;
    private boolean canReceiveDms = true;
    private boolean canReceiveDmsSounds = true;
    private boolean canSeeBroadcasts = true;
    private boolean canSeeTips = true;
    private boolean canAcceptingFriendRequests = true;

    private ItemStack[] allItems;
    private ItemStack[] allArmor;

    private boolean canReport = true;
    private boolean canRequest = true;
    private boolean hasVoted = false;
    private boolean isVanished = false;
    private boolean isStaffMode = false;
    private boolean isFrozen = false;
    private boolean isSynced = false;

    private ScoreBoard modModeBoard;

    private boolean isGrantEditing = false;
    private Document grantTarget = null;
    private Rank grantRank = null;
    private long grantDuration;
    private boolean grantPerm;
    private String grantScope;

    private boolean isGrantDurationEditing = false;
    private Document grantDurationTarget = null;
    private Rank grantDurationRank = null;
    private boolean grantDurationPerm;
    private String grantDurationScope;

    private boolean isReasonEditing = false;
    private PunishmentType reasonType = null;
    private String reasonTarget = null;

    private boolean currentlyMuted;
    private boolean currentlyBanned;
    private boolean currentlyOnline;

    private LanguageType language;
    private PermissionAttachment attachment;

    private long chatCooldown = 1L;
    private long commandCooldown = 1L;

    private Date lastJoined;

    private String lastJoin;
    private String firstJoin;

    private AchievementData achievementData;
    private PotionMessageType potionMessageType;

    public PotPlayer(UUID uuid) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
        this.name = player.getName();
        this.media = new Media();
        this.lastJoined = new Date();
        this.syncCode = SaltUtil.getRandomSaltedString(6);

        this.attachment = this.player.addAttachment(CorePlugin.getInstance());

        this.loadPlayerData();
        profilePlayers.put(uuid, this);
    }

    public void saveWithoutRemove() {
        Document document = new Document("_id", this.uuid);

        document.put("name", this.name);
        document.put("uuid", this.uuid.toString());
        document.put("canSeeStaffMessages", this.canSeeStaffMessages);
        document.put("canSeeTips", this.canSeeTips);
        document.put("canReceiveDms", this.canReceiveDms);
        document.put("canSeeGlobalChat", this.canSeeGlobalChat);
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

        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", uuid), document, new ReplaceOptions().upsert(true)));
    }

    public void savePlayerData() {
        Document document = new Document("_id", this.uuid);

        document.put("name", this.name);
        document.put("uuid", this.uuid.toString());
        document.put("canSeeStaffMessages", this.canSeeStaffMessages);
        document.put("canSeeTips", this.canSeeTips);
        document.put("canReceiveDms", this.canReceiveDms);
        document.put("canSeeGlobalChat", this.canSeeGlobalChat);
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

        CorePlugin.getInstance().getMongoThread().execute(() -> CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().replaceOne(Filters.eq("_id", uuid), document, new ReplaceOptions().upsert(true)));

        syncCodes.remove(this.syncCode);
        profilePlayers.remove(uuid);
    }

    public void loadPlayerData() {
        Document document = CorePlugin.getInstance().getCoreDatabase().getPlayerCollection().find(Filters.eq("_id", this.uuid)).first();
        if (document == null) return;

        this.name = document.getString("name");
        if (document.getBoolean("canSeeStaffMessages") != null) {
            this.canSeeStaffMessages = document.getBoolean("canSeeStaffMessages");
        }
        if (document.getBoolean("canSeeTips") != null) {
            this.canSeeTips = document.getBoolean("canSeeTips");
        }
        if (document.getBoolean("canReceiveDms") != null) {
            this.canReceiveDms = document.getBoolean("canReceiveDms");
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

        syncCodes.put(this.syncCode, this.player.getName());

        this.setupPlayer();
        if (CorePlugin.NAME_MC_REWARDS) this.checkVoting();

        if (document.get("allPrefixes") != null) {
            if (player.hasPermission("scandium.prefixes.all")) {
                List<String> prefixes = new ArrayList<>();
                Prefix.getPrefixes().forEach(prefix -> prefixes.add(prefix.getName()));
                this.getAllPrefixes().addAll(prefixes);
            } else if (!player.hasPermission("scandium.prefixes.all")) {
                List<String> prefixes = ((List<String>) document.get("allPrefixes"));
                this.allPrefixes.addAll(prefixes);
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
            this.getPunishments()
                    .stream()
                    .filter(punishment -> punishment.getPunishmentType().equals(PunishmentType.MUTE))
                    .filter(Punishment::isActive)
                    .filter(punishment -> !punishment.isRemoved())
                    .forEach(punishment -> this.currentlyMuted = true);

            this.getPunishments()
                    .stream()
                    .filter(punishment -> punishment.getPunishmentType().equals(PunishmentType.BAN))
                    .filter(Punishment::isActive)
                    .filter(punishment -> !punishment.isRemoved())
                    .forEach(punishment -> this.currentlyBanned = true);
        });

        this.currentlyOnline = true;

        Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), this::saveWithoutRemove, 10 * 20L);
    }

    public Grant getActiveGrant() {
        return this.getAllGrants()
                .stream()
                .sorted(Comparator.comparingLong(Grant::getDateAdded).reversed()).collect(Collectors.toList())
                .stream()
                .filter(Objects::nonNull)
                .filter(Grant::isActive)
                .filter(grant -> !grant.getRank().isHidden())
                .filter(grant -> (grant.getScope() == null || grant.getScope().equals("global") || (grant.getScope().equals(CorePlugin.getInstance().getServerName()))))
                .findFirst()
                .orElseGet(() -> new Grant(null, Objects.requireNonNull(Rank.getDefault()), System.currentTimeMillis(), -1L, "Automatic Grant (Default)", true, true));
    }

    public void setupPlayer() {
        this.setupDisplay();
        this.resetPermissions();
        this.setupPermissions();
        this.setupPlayerTag();
        this.setupPlayerList();
    }

    public void setupDisplay() {
        if (this.player != null) {
            Grant grant;

            try {
                grant = this.getActiveGrant();
            } catch (Exception ignored) {
                grant = new Grant(null, Objects.requireNonNull(Rank.getDefault()), System.currentTimeMillis(), -1L, "Automatic Grant (Default)", true, true);
                this.getAllGrants().add(grant);
            }

            this.player.setDisplayName(Color.translate(grant.getRank().getColor() + player.getName()));
        }
    }

    public void setupPlayerList() {
        player.setPlayerListName(Color.translate(this.getActiveGrant().getRank().getColor() + (this.customColor != null ? this.customColor : "") + this.player.getName()));
        player.recalculatePermissions();
    }

    public void resetPermissions() {
        if (!this.attachment.getPermissions().isEmpty()) this.attachment.getPermissions().keySet().forEach(s -> this.attachment.unsetPermission(s));
    }

    public void setupPermissions() {
        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
            this.getAllGrants().stream()
                    .filter(Objects::nonNull)
                    .filter(grant -> !grant.isExpired())
                    .filter(grant -> (grant.getScope() == null || (grant.getScope().equals("global") || (grant.getScope().equals(CorePlugin.getInstance().getServerName())))))
                    .forEach(grant -> {
                        grant.getRank().getPermissions().forEach(s -> this.attachment.setPermission(s.replace("-", ""), !s.startsWith("-")));
                        grant.getRank().getInheritance().stream()
                                .map(Rank::getByUuid)
                                .filter(Objects::nonNull)
                                .forEach(rank -> rank.getPermissions().forEach(permission -> this.attachment.setPermission(permission.replace("-", ""), !permission.startsWith("-"))));
                    });
        });
    }

    public void setupPlayerTag() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (this.getActiveGrant().getRank().getColor() != null) {
                if (this.isStaffMode()) {
                    NameTagExternal.setupStaffModeTag(player, this.player);
                } else if (this.isVanished()) {
                    NameTagExternal.setupVanishTag(player, this.player);
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