package com.solexgames.core.player.report;

import com.solexgames.core.CorePlugin;
import com.solexgames.core.util.SaltUtil;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author GrowlyX
 * @since 4/10/2021
 */

@Data
public class Report {

    private final String id;
    private final String reason;

    private final String reporterName;
    private final UUID reporterUuid;

    private final String targetName;
    private final UUID targetUuid;

    private UUID resolverUuid;
    private String resolverName;

    private boolean resolved = false;

    public Report(String reason, Player reporter, Player target) {
        this.id = SaltUtil.getRandomSaltedString(5);
        this.reason = reason;

        this.reporterName = reporter.getName();
        this.reporterUuid = reporter.getUniqueId();

        this.targetName = target.getName();
        this.targetUuid = target.getUniqueId();

        // test

        CorePlugin.getInstance().getReportManager().registerReport(this);
    }
}
