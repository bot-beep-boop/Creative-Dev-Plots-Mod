package me.techstreet.cdp.mixin;

import me.techstreet.cdp.events.ChatRecievedEvent;
import me.techstreet.cdp.utils.MessageGrabber;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MMessageListener {
    private static long lastPatchCheck = 0;
    private static long lastBuildCheck = 0;
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private boolean motdShown = false;

    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        if (MessageGrabber.isActive()) {
            MessageGrabber.supply(packet.getMessage());

            if (MessageGrabber.isSilent()) {
                ci.cancel();
            }
        }

        ChatRecievedEvent.onMessage(packet.getMessage(), ci);
    }
}