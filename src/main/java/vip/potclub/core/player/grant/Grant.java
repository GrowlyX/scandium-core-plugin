package vip.potclub.core.player.grant;

import lombok.Getter;
import lombok.Setter;
import vip.potclub.core.CorePlugin;
import vip.potclub.core.player.ranks.Rank;
import vip.potclub.core.util.SaltUtil;

import java.util.UUID;

@Getter
@Setter
public class Grant {

    private String id;

    private UUID issuer;
    private UUID rankId;

    private long dateAdded;
    private long duration;

    private String reason;
    private boolean active;
    private boolean permanent;

    public Grant(UUID issuer, Rank rank, long dateAdded, long duration, String reason, boolean active, boolean permanent) {
        this.issuer = issuer;
        this.rankId = rank.getUuid();
        this.dateAdded = dateAdded;
        this.duration = duration;
        this.reason = reason;
        this.active = active;
        this.permanent = permanent;

        this.id = SaltUtil.getRandomSaltedString();
    }

    public Rank getRank() {
        return Rank.getByUuid(this.rankId);
    }

    public String toJson() {
        return CorePlugin.GSON.toJson(this);
    }

    public boolean isExpired() {
        return !this.active || System.currentTimeMillis() >= this.dateAdded + this.duration;
    }
}