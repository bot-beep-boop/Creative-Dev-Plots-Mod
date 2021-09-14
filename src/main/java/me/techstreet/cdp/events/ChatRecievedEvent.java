package me.techstreet.cdp.events;

import me.techstreet.cdp.Main;
import me.techstreet.cdp.features.DiscordRPC;
import me.techstreet.cdp.utils.Mode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ChatRecievedEvent {

    public static int cancelMsgs = 0;
    public static String currentPatch = "none";

    public static boolean PLOT = false;
    public static String locateParser_id = "none";
    public static String locateParser_name = "none";
    public static String locateParser_node = "none";
    public static String locateParser_command = "none";

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
            currentPatch = text.replace("Current patch: ", "");
            currentPatch = currentPatch.replace(" See the patch notes with /patch!", "");
        }

        if (text.startsWith("FORWARD_DATA:Heartbeat")) {
            Main.TICK_COUNTER = 0;

            cancel = true;
        }

        if (text.startsWith("FORWARD_DATA:DiscordRPC:")) {
            String data = text.replaceFirst("FORWARD_DATA:DiscordRPC:", "");
            System.out.println("debug_call_rpc");
            DiscordRPC.update(data);
            System.out.println("debug_rpc_callback");
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
