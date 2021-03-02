package com.solexgames.core.player.grant;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.player.ranks.Rank;
import com.solexgames.core.util.SaltUtil;
import lombok.Getter;
import lombok.Setter;

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

    private String issuedServer;
    private String scope = "global";

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

    public Grant(UUID issuer, Rank rank, long dateAdded, long duration, String reason, boolean active, boolean permanent, String scope) {
        this.issuer = issuer;
        this.rankId = rank.getUuid();
        this.dateAdded = dateAdded;
        this.duration = duration;
        this.reason = reason;
        this.active = active;
        this.permanent = permanent;
        this.scope = scope;

        this.id = SaltUtil.getRandomSaltedString();
    }

    public boolean isGlobal() {
        return this.scope.equals("global");
    }

    public boolean isApplicable() {
        return this.scope.equals(CorePlugin.getInstance().getServerName());
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
