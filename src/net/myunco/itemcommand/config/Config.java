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

    public static void loadConfig(ItemCommand plugin) {
        plugin.saveDefaultConfig();
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        YamlConfiguration config = loadConfiguration(configFile);
        version = config.getInt("version");
        language = config.getString("language", "zh_cn");
        Language.loadLanguage(language);
        checkUpdate = config.getBoolean("checkUpdate", true);
        cancelLeftEvent = config.getBoolean("cancelLeftEvent", true);
        cancelRightEvent = config.getBoolean("cancelRightEvent", true);
        cancelHeldEvent = config.getBoolean("cancelHeldEvent", false);
        cancelInvLeftEvent = config.getBoolean("cancelInvLeftEvent", true);
        cancelInvRightEvent = config.getBoolean("cancelInvRightEvent", true);
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

    public static void saveConfiguration(YamlConfiguration config, File file) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(config.saveToString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
