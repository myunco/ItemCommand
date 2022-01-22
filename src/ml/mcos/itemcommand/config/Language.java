package ml.mcos.itemcommand.config;

import ml.mcos.itemcommand.ItemCommand;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Language {
    public static ItemCommand plugin = ItemCommand.getPlugin();
    public static int version;
    public static String logPrefix;
    public static String messagePrefix;
    public static String languageVersionError;
    public static String languageVersionOutdated;
    public static String languageUpdateComplete;
    public static String configVersionError;
    public static String configVersionOutdated;
    public static String configUpdateComplete;
    public static String updateFoundNewVersion;
    public static String updateMajorUpdate;
    public static String updateDownloadLink;
    public static String updateCheckFailure;
    public static String updateCheckException;

    public static void loadLanguage(String language) {
        if (language == null || !language.matches("[a-zA-Z]{2}[_-][a-zA-Z]{2}")) {
            plugin.getLogger().severe("§4语言文件名称格式错误: " + language);
        }
        String langPath = "lang/" + language + ".yml";
        File lang = new File(plugin.getDataFolder(), langPath);
        saveDefaultLanguage(lang, langPath);
        YamlConfiguration config = Config.loadConfiguration(lang);
        version = config.getInt("version");
        logPrefix = config.getString("log-prefix", "[ItemCommand] ");
        messagePrefix = config.getString("message-prefix", "§8[§3ItemCommand§8] ");
        languageVersionError = config.getString("language-version-error", "语言文件版本错误: ");
        languageVersionOutdated = config.getString("language-version-outdated", "§e当前语言文件版本：§a{0} §c最新版本：§b{1} §6需要更新.");
        languageUpdateComplete = config.getString("language-update-complete", "§a语言文件更新完成!");
        configVersionError = config.getString("config-version-error", "配置文件版本错误: ");
        configVersionOutdated = config.getString("config-version-outdated", "§e当前配置文件版本：§a{0} §c最新版本：§b{1} §6需要更新.");
        configUpdateComplete = config.getString("config-update-complete", "§a配置文件更新完成!");
        languageUpdate(config, lang);
        updateFoundNewVersion = config.getString("update-found-new-version", "§c发现新版本可用! §b当前版本: {0} §d最新版本: {1}");
        updateMajorUpdate = config.getString("update-major-update", "§e(有大更新)");
        updateDownloadLink = config.getString("update-download-link", "§a下载地址: ");
        updateCheckFailure = config.getString("update-check-failure", "§e检查更新失败, 状态码: ");
        updateCheckException = config.getString("update-check-exception", "§4检查更新时发生IO异常.");
    }

    private static void saveDefaultLanguage(File lang, String langPath) {
        if (!lang.exists()) {
            if (plugin.classLoader().getResource(langPath) == null) {
                InputStream in = plugin.getResource("lang/zh_cn.yml");
                if (in != null) {
                    try {
                        OutputStream out = new FileOutputStream(lang);
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) != -1) {
                            out.write(buf, 0, len);
                        }
                        out.close();
                        in.close();
                        plugin.logMessage("§a语言文件: " + lang.getName() + " 不存在, 已自动创建。");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    plugin.logMessage("§4语言文件: " + lang.getName() + " 不存在, 并且在插件内找不到默认语言文件: zh_cn.yml");
                }
            } else {
                plugin.saveResource(langPath, true);
            }
        }
    }

    private static void languageUpdate(YamlConfiguration config, File lang) {
        int currentVersion = 1;
        if (version < currentVersion) {
            //语言文件目前只有一个版本 升级代码暂时不写
            plugin.getLogger().warning(Language.languageVersionError + Language.version);
            config.set("version", 1);
            Config.saveConfiguration(config, lang);
        }
    }

    public static String replaceArgs(String msg, Object... args) {
        for (int i = 0; i < args.length; i++) {
            msg = msg.replace("{0}".replace('0', (char) (i + 48)), args[i].toString());
        }
        return msg;
    }

}
