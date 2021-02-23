package vip.potclub.core.util.external;

import vip.potclub.core.CorePlugin;

import java.net.URL;
import java.util.Scanner;

public final class NameMCExternal {

    public static boolean hasVoted(String uuid) throws Exception {
        try (Scanner scanner = new Scanner(new URL("https://api.namemc.com/server/" + CorePlugin.getInstance().getConfig().getString("settings.namemc-ip") + "/likes?profile=" + uuid).openStream()).useDelimiter("\\A")) {
            return Boolean.parseBoolean(scanner.next());
        }
    }
}
