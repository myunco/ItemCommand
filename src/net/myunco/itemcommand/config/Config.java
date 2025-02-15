package net.myunco.itemcommand.config;

import net.myunco.itemcommand.ItemCommand;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class Config {
    public static int version;
    public static String language;
    public static boolean checkUpdate;
    public static boolean cancelLeftEvent;
    public static boolean cancelRightEvent;
    public static boolean cancelHeldEvent;
    public static boolean cancelInvLeftEvent;
    public static boolean cancelInvRightEvent;
    public static boolean usePerformCommand;

    public static void loadConfig(ItemCommand plugin) {
        plugin.saveDefaultConfig();
        YamlConfiguration config = loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        version = config.getInt("version");
        language = config.getString("language", "zh_cn");
        Language.loadLanguage(language);
        updateConfiguration(plugin, config);
        checkUpdate = config.getBoolean("checkUpdate", true);
        cancelLeftEvent = config.getBoolean("cancelLeftEvent", true);
        cancelRightEvent = config.getBoolean("cancelRightEvent", true);
        cancelHeldEvent = config.getBoolean("cancelHeldEvent", false);
        cancelInvLeftEvent = config.getBoolean("cancelInvLeftEvent", true);
        cancelInvRightEvent = config.getBoolean("cancelInvRightEvent", true);
        usePerformCommand = config.getBoolean("usePerformCommand", false);
    }

    public static YamlConfiguration loadConfiguration(File file) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append('\n');
                }
            } finally {
                reader.close();
            }
            config.loadFromString(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return config;
    }

    public static void updateConfiguration(ItemCommand plugin, YamlConfiguration config) {
        int latestVersion = 2;
        version = config.getInt("version");
        if (version < latestVersion) {
            plugin.logMessage(Language.replaceArgs(Language.configVersionOutdated, version, latestVersion));
            if (version == 1) {
                config.set("usePerformCommand", false);
            } else {
                plugin.logMessage(Language.configVersionError + version);
                return;
            }
            plugin.logMessage(Language.configUpdateComplete);
            version = latestVersion;
            config.set("version", latestVersion);
            saveConfiguration(config, new File(plugin.getDataFolder(), "config.yml"));
        }
    }

    public static void saveConfiguration(YamlConfiguration config, File file) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(config.saveToString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
