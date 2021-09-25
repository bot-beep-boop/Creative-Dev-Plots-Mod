package me.techstreet.cdp;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class Main implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger(Main.class);
	public static int TICK_COUNTER = 0;
	public static MinecraftClient MC = MinecraftClient.getInstance();
	public static HashMap<String, String> PREFIX_CACHE = new HashMap<>();
	public static boolean ON_PLOT = false;
	public static JsonObject COMMANDS = null;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		Runtime.getRuntime().addShutdownHook(new Thread(this::onClose));

		LOGGER.info("Initialising CDP");

		try {
			String sURL = "https://raw.githubusercontent.com/TechStreetDev/Creative-Dev-Plots-Mod/1.16.5/commands.json";
			URL url = new URL(sURL);
			URLConnection request = url.openConnection();
			request.connect();

			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
			COMMANDS = root.getAsJsonObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//DiscordRPC.init();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			TICK_COUNTER += 1;

			if (TICK_COUNTER == 10) {
				TICK_COUNTER = 0;
				ON_PLOT = false;
				//DiscordRPC.close();
			}
		});
	}

	public void onClose() {
	}

	public static void log(Level level, String message) {
		LOGGER.log(level, "[CBP Mod] " + message);
	}
}
