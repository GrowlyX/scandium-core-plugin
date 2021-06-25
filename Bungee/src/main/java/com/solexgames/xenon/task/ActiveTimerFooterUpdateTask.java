package com.solexgames.xenon.task;

import com.solexgames.xenon.CorePlugin;
import com.solexgames.xenon.util.MOTDUtil;

/**
 * @author GrowlyX
 * @since 6/24/2021
 */

public class ActiveTimerFooterUpdateTask implements Runnable {

    /**
     * This is done to prevent players from refreshing the server list and format the footer each time
     */

    @Override
    public void run() {
        CorePlugin.getInstance().setMotdTimerFooter(MOTDUtil.getCenteredMotd(CorePlugin.getInstance().getXenonTopicTimer().getFormattedFooter()));
    }
}
