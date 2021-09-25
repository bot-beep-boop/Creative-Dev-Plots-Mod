package me.techstreet.cdp.features;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import me.techstreet.cdp.Main;
import me.techstreet.cdp.utils.Mode;
import org.apache.logging.log4j.Level;

import java.time.OffsetDateTime;

public class DiscordRPC {

    public static boolean delayRPC = false;

    public static RichPresence.Builder builder;
    private static Mode oldState = Mode.UNKNOWN;
    private static OffsetDateTime time;
    private static IPCClient client;

    public static void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Main.log(Level.INFO, "[DiscordRPC] Closing Discord hook.");
            try {
                close();
            } catch (Exception e) {
                Main.log(Level.ERROR, "[DiscordRPC] Error while closing Discord hook.");
            }
        }));

        Main.log(Level.INFO, "[DiscordRPC] Starting Discord hook.");
        client = new IPCClient(887370487565541378L);
        client.setListener(new IPCListener() {
            @Override
            public void onReady(IPCClient client) {
                RichPresence.Builder builder = new RichPresence.Builder();
                builder.setDetails("Playing");
                DiscordRPC.builder = builder;
            }
        });

        Main.log(Level.INFO, "[DiscordRPC] Started Discord hook.");

    }

    public static void connect() {
        if(!isConnected()){
            try {
                Main.log(Level.INFO, "[DiscordRPC] Connecting to discord client.");
                client.connect();
            } catch (NoDiscordClientException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close() {
        if(isConnected()) {
            Main.log(Level.INFO, "[DiscordRPC] Closing discord hook.");
            client.close();
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

        if (!mode.equals(oldState)) {
            oldState = mode;

            Main.log(Level.INFO, "[DiscordRPC] Starting discord RPC.");

            new Thread(() -> {

                if (!isConnected()) {
                    connect();
                }

                Main.log(Level.INFO, "[DiscordRPC] Building the presence.");

                // Set parameters for the Core
                RichPresence.Builder presence = new RichPresence.Builder();

                presence.setDetails(dataParts[0]);

                if (!dataParts[1].equals("()")) {
                    presence.setState(data.split(":")[1]);
                }

                // Setting a start time causes an "elapsed" field to appear
                time = OffsetDateTime.now();
                presence.setStartTimestamp(time);

                // Make a "cool" image show up
                presence.setLargeImage("large", "/join 50020");

                // Finally, update the current activity to our activity
                Main.log(Level.INFO, "[DiscordRPC] Pushing to discord.");
                client.sendRichPresence(presence.build());
            }).start();
        }
    }

    private static boolean isConnected() {
        return client.getStatus() == PipeStatus.CONNECTED;
    }
}
