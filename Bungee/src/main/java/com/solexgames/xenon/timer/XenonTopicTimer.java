package com.solexgames.xenon.timer;

import com.solexgames.xenon.CorePlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
                String.format(CorePlugin.getInstance().getTimerFormatEnded(), this.name) :
                String.format(CorePlugin.getInstance().getTimerFormatCountdown(), this.name, formatted);
    }
}
