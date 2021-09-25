package me.techstreet.cdp.mixin;

import com.google.gson.JsonElement;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import me.techstreet.cdp.Main;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestor.class)
public class MCommandSuggestor {
    @Shadow
    private CompletableFuture<Suggestions> pendingSuggestions;

    @Inject(method = "method_30104", at = @At("RETURN"), cancellable = true)
    public void getSuggestionList(Suggestions suggestions, CallbackInfoReturnable<List<Suggestion>> cir) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen instanceof ChatScreen) {
            ChatScreen chatScreen = (ChatScreen) screen;
            TextFieldWidget textFieldWidget = (TextFieldWidget) chatScreen.children().stream().filter(element -> element instanceof TextFieldWidget).toArray()[0];

            List<Suggestion> dest = cir.getReturnValue();

            if (Main.COMMANDS != null) {
                for (Map.Entry<String, JsonElement> entry : Main.COMMANDS.entrySet()) {
                    String command = String.format("@%s", entry.getKey());
                    dest.add(new Suggestion(StringRange.between(textFieldWidget.getCursor(), textFieldWidget.getCursor()), command));
                }
            } else {
                Main.MC.player.sendMessage(new LiteralText("§cA unknown error occured please send your logs in the CDP discord!"), false);
            }

            cir.setReturnValue(dest);
            cir.cancel();
        }
    }
}