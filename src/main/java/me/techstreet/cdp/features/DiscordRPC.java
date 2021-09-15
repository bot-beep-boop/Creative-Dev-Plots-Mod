package me.techstreet.cdp.features;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import me.tecc.dgsdk.DGameSDK;
import me.techstreet.cdp.Main;
import me.techstreet.cdp.utils.Mode;
import org.apache.logging.log4j.Level;

import java.time.Instant;

public class DiscordRPC {

    private static Mode LAST_MODE = Mode.UNKNOWN;
    private static Core INSTANCE;

    public static void init() {
        try {
            Main.log(Level.DEBUG, "Initialising Discord RPC...");
            DGameSDK.init();
            CreateParams params = new CreateParams();
            params.setClientID(887370487565541378L);
            params.setFlags(CreateParams.getDefaultFlags());
            // Create the Core
            INSTANCE = new Core(params);
            Main.log(Level.DEBUG, "Discord RPC initialised!");
        } catch (Throwable t) {
            Main.log(Level.ERROR, "Couldn't initialise Discord RPC!");
            INSTANCE = null;
        }
    }

    public static void update(String data) {
        String[] dataParts = data.split(":");
        Mode mode;

        try {
            mode = Mode.valueOf(dataParts[2]);
        } catch (Exception e) {
            mode = Mode.UNKNOWN;
        }

        if (!mode.equals(LAST_MODE)) {
            LAST_MODE = mode;

            new Thread(() -> {
                // Set parameters for the Core
                Activity activity = new Activity();

                if (dataParts.length > 0) {
                    activity.setDetails(dataParts[0]);
                }

                if (dataParts.length > 1) {
                    if (!dataParts[1].equals("()")) {
                        activity.setState(data.split(":")[1]);
                    }
                }

                // Setting a start time causes an "elapsed" field to appear
                activity.timestamps().setStart(Instant.now());

                // Make a "cool" image show up
                activity.assets().setLargeImage("large");
                activity.assets().setLargeText("/join 50020");

                // Finally, update the current activity to our activity
                INSTANCE.activityManager().updateActivity(activity);

                INSTANCE.runCallbacks();
            }).start();
        }
    }

    public static void close() {
        try {
            INSTANCE.activityManager().clearActivity();
            LAST_MODE = Mode.UNKNOWN;
        } catch (Exception e) {
            return;
        }
    }

    public static void disconnect() {
        try {
            INSTANCE.close();
        } catch (Exception e) {
            return;
        }
    }
}
