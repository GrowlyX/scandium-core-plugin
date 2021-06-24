package com.solexgames.xenon.timer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.time.DurationFormatUtils;

import java.util.Date;

/**
 * @author GrowlyX
 * @since 6/24/2021
 */

@Getter @Setter
@RequiredArgsConstructor
public class XenonTopicTimer {

    private String name = "Default";
    private Date endsAt = new Date();

    private boolean active = false;

    public String getFormattedFooter() {
        final Date now = new Date();
        final String formatted = DurationFormatUtils.formatDurationWords(this.endsAt.getTime() - now.getTime(), true, true);

        return this.endsAt.before(now) ?
                ChatColor.YELLOW + this.name + ChatColor.GRAY + " has commenced, have fun!" :
                ChatColor.YELLOW + this.name + ChatColor.GRAY + " will start in " + ChatColor.GOLD + formatted;
    }
}
