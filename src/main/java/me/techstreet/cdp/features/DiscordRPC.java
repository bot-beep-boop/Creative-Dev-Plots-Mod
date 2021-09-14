package me.techstreet.cdp.features;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import me.tecc.dgsdk.DGameSDK;
import me.techstreet.cdp.Main;
import me.techstreet.cdp.utils.Mode;

import java.time.Instant;

public class DiscordRPC {

    private static String oldData = "";
    private static Core INSTANCE;

    public static void init() {
        if (INSTANCE != null) return;
        try {
            Main.LOGGER.debug("Initialising Discord RPC");
            DGameSDK.init();
            CreateParams params = new CreateParams();
            params.setClientID(887370487565541378L);
            params.setFlags(CreateParams.getDefaultFlags());
            // Create the Core
            INSTANCE = new Core(params);
            Main.LOGGER.debug("Discord RPC initialised");
        } catch (Throwable t) {
            Main.LOGGER.error("Couldn't initialise Discord RPC", t);
            INSTANCE = null;
        }
    }

    public static void update(String data) {
        if (!oldData.equals(data)) {
            Main.LOGGER.debug("Discord RPC updating");

            oldData = data;
            // Set parameters for the Core
            Main.LOGGER.debug("Creating activity");
            Activity activity = new Activity();

            String[] dataParts = data.split(":");
            if (dataParts.length > 0) {
                activity.setDetails(dataParts[0]);
            }
            if (dataParts.length > 1) {
                activity.setState(data.split(":")[1]);
            }

            // Setting a start time causes an "elapsed" field to appear
            activity.timestamps().setStart(Instant.now());

            // Make a "cool" image show up
            activity.assets().setLargeImage("large");

            // Finally, update the current activity to our activity
            Main.LOGGER.debug("Updating activity");
            INSTANCE.activityManager().updateActivity(activity);
        }
        INSTANCE.runCallbacks();
    }

    public static void close() {
        try {
            INSTANCE.activityManager().clearActivity();
            oldData = "";
            INSTANCE.close();
            INSTANCE = null;
        } catch (Exception e) {
            return;
        }
    }
}
