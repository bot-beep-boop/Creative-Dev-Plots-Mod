package me.techstreet.cdp.features;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import me.tecc.dgsdk.DGameSDK;
import me.techstreet.cdp.utils.Mode;

import java.time.Instant;

public class DiscordRPC {

    private static String oldData = "";
    private static Core INSTANCE;

    public static void init() {
        DGameSDK.init();

        try (CreateParams params = new CreateParams()) {
            params.setClientID(887370487565541378L);
            params.setFlags(CreateParams.getDefaultFlags());
            // Create the Core
            try (Core core = new Core(params)) {
                INSTANCE = core;
            }
        }
    }

    public static void update(String data) {
        if (!oldData.equals(data)) {
            System.out.println("starting rpc");

            data = oldData;
            // Set parameters for the Core
            try (Activity activity = new Activity()) {
                System.out.println("starting activity");

                activity.setDetails(data.split(":")[0]);
                activity.setState(data.split(":")[1]);

                // Setting a start time causes an "elapsed" field to appear
                activity.timestamps().setStart(Instant.now());

                // Make a "cool" image show up
                activity.assets().setLargeImage("large");

                // Finally, update the current activity to our activity
                System.out.println("updating activity");
                INSTANCE.activityManager().updateActivity(activity);
                System.out.println("updated activiy");
            }
        }
        INSTANCE.runCallbacks();
    }

    public static void close() {
        try {
            INSTANCE.activityManager().clearActivity();
            oldData = "";
        } catch (Exception e) {
            return;
        }
    }
}