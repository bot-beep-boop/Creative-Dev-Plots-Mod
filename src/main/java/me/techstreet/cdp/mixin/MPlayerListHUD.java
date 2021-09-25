package me.techstreet.cdp.mixin;

import me.techstreet.cdp.Main;
import me.techstreet.cdp.utils.TextUtils;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(PlayerListHud.class)
public class MPlayerListHUD {
    private static final Text SPACE = Text.of(" ");
    private static final Text AIR = Text.of("");

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    public void getPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        UUID id = entry.getProfile().getId();
        Text name = entry.getDisplayName() != null ? this.spectatorFormat(entry, entry.getDisplayName().shallowCopy()) : this.spectatorFormat(entry, Team.modifyText(entry.getScoreboardTeam(), new LiteralText(entry.getProfile().getName())));

        if (Main.ON_PLOT) {
            if (Main.PREFIX_CACHE.containsKey(id.toString())) {
                String p = Main.PREFIX_CACHE.get(id.toString());
                Text prefix = TextUtils.colorCodesToTextComponent(p.replace("&", "§"));

                if (!p.startsWith("0")) {
                    name = AIR.copy().append(prefix).append(SPACE).append(name);
                }
            }
        }

        cir.setReturnValue(name);
    }

    private Text spectatorFormat(PlayerListEntry playerListEntry, MutableText mutableText) {
        return playerListEntry.getGameMode() == GameMode.SPECTATOR ? mutableText.formatted(Formatting.ITALIC) : mutableText;
    }
}
