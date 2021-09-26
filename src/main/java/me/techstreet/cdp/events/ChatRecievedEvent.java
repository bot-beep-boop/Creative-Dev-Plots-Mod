package me.techstreet.cdp.events;

import me.techstreet.cdp.Main;
import me.techstreet.cdp.features.DiscordRPC;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ChatRecievedEvent {

    public static int cancelMsgs = 0;

    public static String CURRENT_PATCH = "none";

    public static boolean PLOT = false;

    public static void onMessage(Text message, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String text = message.getString();

        boolean cancel = false;
        boolean display = false;

        if (mc.player == null) {
            return;
        }

        if (cancelMsgs > 0) {
            cancelMsgs--;
            cancel = true;
        }

        if (text.startsWith("Current patch: ")) {
            CURRENT_PATCH = text.replace("Current patch: ", "");
            CURRENT_PATCH = CURRENT_PATCH.replace(" See the patch notes with /patch!", "");
        }

        if (text.startsWith("FORWARD_DATA:PlayerNodeConnect:")) {
            String data = text.replaceFirst("FORWARD_DATA:PlayerNodeConnect:", "")
                    .replace("\n", "");
            Main.MC.player.sendChatMessage("/server " + data);
            cancel = true;
        }

        if (text.startsWith("FORWARD_DATA:PlayerPlotConnect:")) {
            String data = text.replaceFirst("FORWARD_DATA:PlayerPlotConnect:", "")
                    .replace("\n", "");
            Main.MC.player.sendChatMessage("/join " + data);
            cancel = true;
        }

        if (text.startsWith("FORWARD_DATA:Heartbeat")) {
            Main.TICK_COUNTER = 0;
            Main.ON_PLOT = true;
            cancel = true;
        }

        if (text.startsWith("FORWARD_DATA:DiscordRPC:")) {
            String data = text.replaceFirst("FORWARD_DATA:DiscordRPC:", "");
            DiscordRPC.update(data);

            cancel = true;
        }

        if (text.startsWith("FORWARD_DATA:UserPrefix:")) {
            String data = text.replaceFirst("FORWARD_DATA:DiscordRPC:", "");
            String[] dataParts = data.split(":");

            Main.PREFIX_CACHE.put(dataParts[2], dataParts[3]);

            cancel = true;
        }

        if (cancel) {
            if (display) {
                Main.log(Level.INFO, "[CANCELED] " + text);
            }
            ci.cancel();
        }
    }
}
