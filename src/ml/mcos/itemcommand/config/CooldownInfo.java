package ml.mcos.itemcommand.config;

import ml.mcos.itemcommand.ItemCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class CooldownInfo {
    private static final ItemCommand plugin = ItemCommand.getPlugin();
    private static File cooldownFile;
    private static YamlConfiguration cooldownInfo;

    public static void loadCooldownInfo(HashMap<UUID, HashMap<String, Long>> cdMap) {
        cooldownFile = new File(plugin.getDataFolder(), "data/cooldown.yml");
        if (!cooldownFile.exists()) {
            if (!cooldownFile.getParentFile().exists()) {
                cooldownFile.getParentFile().mkdir();
            }
            try {
                cooldownFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Unable to create data/cooldown.yml");
                return;
            }
        }
        cooldownInfo = Config.loadConfiguration(cooldownFile);
        for (String uuid : cooldownInfo.getKeys(false)) {
            ConfigurationSection cdList = cooldownInfo.getConfigurationSection(uuid);
            HashMap<String, Long> playerCdInfo = new HashMap<>();
            if (cdList != null) {
                for (String item : cdList.getKeys(false)) {
                    long cdEndTime = cdList.getLong(item);
                    if (cdEndTime > System.currentTimeMillis()) {
                        playerCdInfo.put(item, cdEndTime);
                    } else {
                        cooldownInfo.set(uuid + "." + item, null);
                    }
                }
            }
            if (playerCdInfo.isEmpty()) {
                cooldownInfo.set(uuid, null);
            } else {
                cdMap.put(UUID.fromString(uuid), playerCdInfo);
            }
        }
        if (cdMap.isEmpty()) {
            cooldownFile.delete();
        } else {
            plugin.getLogger().info("Loaded cooldown information for " + cdMap.size() + " players.");
        }
    }

    public static void putCooldownInfo(String uuid, String item, long time) {
        if (!cooldownInfo.contains(uuid)) {
            cooldownInfo.createSection(uuid);
        }
        cooldownInfo.set(uuid + "." + item, time);
        saveCooldownInfo();
    }

    public static void saveCooldownInfo() {
        if (!cooldownFile.getParentFile().exists() && !cooldownFile.getParentFile().mkdirs()) {
            plugin.getLogger().severe("§4创建数据目录失败.");
            return;
        }
        Config.saveConfiguration(cooldownInfo, cooldownFile);
    }
}
