package com.solexgames.api.example;

import com.solexgames.api.CoreAccess;
import com.solexgames.core.player.global.NetworkPlayer;

/**
 * @author GrowlyX
 * @since 4/2/2021
 * <p>
 * Shows how the {@link CoreAccess} is used.
 */

public class ExampleAccess {

    /**
     * Starts the ExampleAccess class.
     *
     * @param args Startup Arguments
     */
    public static void main(String[] args) {

        // Make sure to make a public variable for this to prevent memory leaks.
        CoreAccess coreAccess = new CoreAccess();
        NetworkPlayer networkPlayer = coreAccess.fetchGlobalProfile("GrowlyX");

        // Do whatever with the methods available in the CoreAccess Class.
        if (networkPlayer != null) {
            System.out.println(networkPlayer.getServerName());
        } else {
            System.out.println("Growly's not online!");
        }
    }
}
