package me.techstreet.cdp;

import me.techstreet.cdp.events.ChatRecievedEvent;
import me.techstreet.cdp.features.DiscordRPC;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger(Main.class);
	public static int TICK_COUNTER = 0;
	public static MinecraftClient MC = MinecraftClient.getInstance();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		Runtime.getRuntime().addShutdownHook(new Thread(this::onClose));

		LOGGER.info("Initialising CDP");

		DiscordRPC.init();
		DiscordRPC.close();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			TICK_COUNTER += 1;

			if (TICK_COUNTER == 10) {
				TICK_COUNTER = 0;
				ChatRecievedEvent.PLOT = false;
				DiscordRPC.close();
			}
		});
	}

	public void onClose() {
		DiscordRPC.close();
		DiscordRPC.disconnect();
	}

	public static void log(Level level, String message) {
		LOGGER.log(level, "[CBP Mod] " + message);
	}
}
