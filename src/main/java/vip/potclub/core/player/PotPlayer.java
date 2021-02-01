package vip.potclub.core.player;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.enums.LanguageType;
import vip.potclub.core.media.Media;
import vip.potclub.core.player.grant.Grant;
import vip.potclub.core.player.prefixes.Prefix;
import vip.potclub.core.player.punishment.Punishment;
import vip.potclub.core.player.punishment.PunishmentType;
import vip.potclub.core.player.ranks.Rank;
import vip.potclub.core.util.Color;
import vip.potclub.core.util.SaltUtil;

import java.beans.ConstructorProperties;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
public class PotPlayer {

    @Getter public static Map<UUID, PotPlayer> profilePlayers = new HashMap<>();
    @Getter public static Map<String, String> syncCodes = new HashMap<>();

    private List<Punishment> punishments = new ArrayList<>();
    private List<String> friends = new ArrayList<>();
    private List<String> ownedPrefixes = new ArrayList<>();
    private List<Grant> allGrants = new ArrayList<>();

    private UUID uuid;
    private Player player;
    private Media media;
    private Prefix appliedPrefix;

    private Player lastRecipient;

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

    private boolean canReport = true;
    private boolean canRequest = true;
    private boolean isVanished = false;
    private boolean isSynced = false;

    private boolean currentlyMuted;
    private boolean currentlyBanned;
    private boolean currentlyOnline;

    private LanguageType language;
    private PermissionAttachment attachment;

    private long chatCooldown;

    private final Date lastJoined = new Date();

    private String lastJoin;
    private String firstJoin;

    @ConstructorProperties({"uuid"})
    public PotPlayer(UUID uuid) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
        this.name = player.getName();
        this.media = new Media();

        loadPlayerData();

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
        document.put("rankName", this.getActiveGrant().getRank());
        document.put("discordSyncCode", this.syncCode);
        document.put("syncDiscord", this.syncDiscord);
        document.put("isSynced", this.isSynced);
        document.put("language", (this.language != null ? this.language.getLanguageName() : LanguageType.ENGLISH.getLanguageName()));
        document.put("currentlyOnline", this.currentlyOnline);

        document.put("discord", this.media.getDiscord());
        document.put("twitter", this.media.getTwitter());
        document.put("instagram", this.media.getInstagram());
        document.put("youtube", this.media.getYoutubeLink());

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
        document.put("rankName", this.getActiveGrant().getRank());
        document.put("discordSyncCode", this.syncCode);
        document.put("syncDiscord", this.syncDiscord);
        document.put("isSynced", this.isSynced);
        document.put("language", (this.language != null ? this.language.getLanguageName() : LanguageType.ENGLISH.getLanguageName()));
        document.put("currentlyOnline", false);

        document.put("discord", this.media.getDiscord());
        document.put("twitter", this.media.getTwitter());
        document.put("instagram", this.media.getInstagram());
        document.put("youtube", this.media.getYoutubeLink());

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
        if (document.getBoolean("canReceiveDmsSounds") != null) {
            this.canReceiveDmsSounds = document.getBoolean("canReceiveDmsSounds");
        }

        this.firstJoin = ((document.getString("firstJoined") != null) ? document.getString("firstJoined") : CorePlugin.FORMAT.format(new Date()));

        if (document.getString("language") != null) {
            this.language = LanguageType.getByName(document.getString("language"));
        } else {
            this.language = LanguageType.ENGLISH;
        }

        if (document.getString("discord") != null) {
            this.media.setDiscord(document.getString("discord"));
        } else {
            this.media.setDiscord("N/A");
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

        CorePlugin.getInstance().getPunishmentManager().getPunishments().forEach(punishment -> {
            if (punishment.getTarget().equals(this.uuid)) {
                this.punishments.add(punishment);
            }
        });

        syncCodes.put(this.syncCode, this.player.getName());

        this.setupAttachment();

        this.currentlyMuted = this.isMuted();
        this.currentlyBanned = this.isBanned();
        this.currentlyOnline = true;

        Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(), this::saveWithoutRemove, 10 * 20L);
    }

    public boolean isMuted() {
        AtomicBoolean yes = new AtomicBoolean(false);
        Punishment.getAllPunishments().forEach(punishment -> {
            if (punishment.getTarget() == this.uuid) {
                if (!punishment.isRemoved() && punishment.isActive()) {
                    if (punishment.getPunishmentType().equals(PunishmentType.MUTE)) {
                        yes.set(true);
                    }
                }
            }
        });

        return yes.get();
    }

    public boolean isBanned() {
        AtomicBoolean yes = new AtomicBoolean(false);
        Punishment.getAllPunishments().forEach(punishment -> {
            if (punishment.getTarget() == this.uuid) {
                if (!punishment.isRemoved() && punishment.isActive()) {
                    if (punishment.getPunishmentType().equals(PunishmentType.BAN)) {
                        yes.set(true);
                    }
                }
            }
        });

        return yes.get();
    }

    public Grant getActiveGrant() {
        Grant toReturn = null;
        for (Grant grant : this.getAllGrants()) {
            if (grant.isActive() && !grant.getRank().defaultRank) {
                toReturn = grant;
            }
        }
        if (toReturn == null) toReturn = new Grant(null, Objects.requireNonNull(Rank.getDefaultRank()), System.currentTimeMillis(), 2147483647L, "Automatic Grant (Default)", true);
        return toReturn;
    }

    public void setupAttachment() {
        if (this.player != null) {
            Grant grant = this.getActiveGrant();
            this.player.setDisplayName(Color.translate(grant.getRank().getColor() + player.getName()));
        }

        this.attachment.getPermissions().keySet().forEach(s -> this.attachment.unsetPermission(s));

        for (Grant grants : this.getAllGrants()) {
            if (grants == null) continue;
            if (grants.isExpired()) continue;
            for (String rankPermissions : grants.getRank().getPermissions()) {
                this.attachment.setPermission(rankPermissions.replace("-", ""), !rankPermissions.startsWith("-"));
            }
            for (UUID rankUuid : grants.getRank().getInheritance()) {
                Rank rank = Rank.getByUuid(rankUuid);
                if (rank != null) {
                    for (String permission3 : rank.getPermissions()) {
                        this.attachment.setPermission(permission3.replace("-", ""), !permission3.startsWith("-"));
                    }
                }
            }
        }

        if (player != null) player.recalculatePermissions();
    }

    public void unMutePlayer() {
        MongoCollection<Document> mongoCollection = CorePlugin.getInstance().getCoreDatabase().getPunishmentCollection();
        CorePlugin.getInstance().getMongoThread().execute(() -> {
            for (Document document : mongoCollection.find()) {
                if (document.get("punishmentType").equals("MUTE")) {
                    if (document.get("target").equals(this.uuid.toString())) {
                        if (!document.get("active").equals(false)) {
                            this.currentlyMuted = false;
                            Punishment punishment = CorePlugin.GSON.fromJson(document.toJson(), Punishment.class);
                            punishment.setActive(false);
                            punishment.setRemoved(true);
                            punishment.savePunishment();
                        }
                    }
                }
            }
        });
    }

    public void unBanPlayer() {
        MongoCollection<Document> mongoCollection = CorePlugin.getInstance().getCoreDatabase().getPunishmentCollection();
        CorePlugin.getInstance().getMongoThread().execute(() -> {
            for (Document document : mongoCollection.find()) {
                if (document.get("punishmentType").equals("BAN")) {
                    if (document.get("target").equals(this.uuid.toString())) {
                        if (!document.get("active").equals(false)) {
                            this.currentlyBanned = false;
                            Punishment punishment = CorePlugin.GSON.fromJson(document.toJson(), Punishment.class);
                            punishment.setActive(false);
                            punishment.setRemoved(true);
                            punishment.savePunishment();
                        }
                    }
                }
            }
        });
    }

    public static PotPlayer getPlayer(Player player) {
        return profilePlayers.get(player.getUniqueId());
    }

    public static PotPlayer getPlayer(UUID uuid) {
        return profilePlayers.get(uuid);
    }

    public static PotPlayer getPlayer(String name) {
        return profilePlayers.get(Bukkit.getPlayer(name).getUniqueId());
    }
}
