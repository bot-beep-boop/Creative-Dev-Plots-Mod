/*
    Credit @tecc
    https://github.com/tecc
 */
package me.techstreet.cdp.features;

import de.jcm.discordgamesdk.Core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DiscordGameSDK {
    public static String DISCORD_SDK_VERSION = "2.5.6";

    public static void init() {
        try {
            Core.init(getDiscordLibrary());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static File getDGSDKDir() {
        File dir = new File(System.getProperty("user.home"), ".dgsdk/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    public static File getDownloadsDir() {
        File dir = new File(getDGSDKDir(), DISCORD_SDK_VERSION);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    public static File getDiscordLibrary() throws IOException {
        String name = "discord_game_sdk";
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
        String suffix;
        if (osName.contains("windows")) {
            suffix = ".dll";
        } else if (osName.contains("linux")) {
            suffix = ".so";
        } else {
            if (!osName.contains("mac os")) {
                throw new RuntimeException("cannot determine OS type: " + osName);
            }

            suffix = ".dylib";
        }

        if (arch.equals("amd64")) {
            arch = "x86_64";
        }

        String zipPath = "lib/" + arch + "/" + name + suffix;
        String pathname = name + suffix;
        String qualifiedPathname = arch + "-" + pathname;
        File qdf = new File(getDownloadsDir(), qualifiedPathname);
        if (qdf.exists()) {
            return qdf;
        } else {
            URL downloadUrl = new URL("https://dl-game-sdk.discordapp.net/" + DISCORD_SDK_VERSION + "/discord_game_sdk.zip");
            ZipInputStream zin = new ZipInputStream(downloadUrl.openStream());

            ZipEntry entry;
            while((entry = zin.getNextEntry()) != null) {
                if (entry.getName().equals(zipPath)) {
                    Files.copy(zin, qdf.toPath(), new CopyOption[0]);
                    zin.close();
                    return qdf;
                }

                zin.closeEntry();
            }

            zin.close();
            throw new UnsupportedOperationException("Invalid platform/arch");
        }
    }
}
