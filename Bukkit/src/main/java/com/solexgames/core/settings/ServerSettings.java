package com.solexgames.core.settings;

import lombok.Data;

@Data
public class ServerSettings {

    private String tabHeader;
    private String tabFooter;
    private String chatFormat;
    private String alertFormat;

    private boolean tabEnabled = true;
    private boolean canJoin = false;
    private boolean colorEnabled = true;
    private boolean nameMcEnabled = true;
    private boolean chatFormatEnabled = true;
    private boolean antiSpamEnabled = true;
    private boolean antiCommandSpamEnabled = true;
    private boolean staffAlertsEnabled = false;
    private boolean twoFactorEnabled = true;

}
