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
    public static String loadItemErrorNotMatch;
    public static String loadItemErrorUnknownType;
    public static String loadItemErrorUnknownTrigger;
    public static String actionExecuteErrorSound;
    public static String actionExecuteErrorGiveMoneyNotFoundEconomy;
    public static String actionExecuteErrorGiveMoneyNotFoundEconomyTip;
    public static String actionExecuteErrorGiveMoneyInvalidValue;
    public static String actionExecuteErrorGivePointsNotFoundPoints;
    public static String actionExecuteErrorGivePointsNotFoundPointsTip;
    public static String actionExecuteErrorGivePointsInvalidValue;
    public static String actionExecuteErrorTitleArgsError;
    public static String actionExecuteErrorTitleNotSupport;
    public static String actionExecuteErrorActionBarNotSupport;

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
        loadItemErrorNotMatch = config.getString("load-item-error-not-match", "加载 {0} 时出错! name、lore、type 至少需要提供一个, 当前全未提供");
        loadItemErrorUnknownType = config.getString("load-item-error-unknown-type", "加载 {0} 时出错! 未知的物品类型: type: {1}");
        loadItemErrorUnknownTrigger = config.getString("load-item-error-unknown-trigger", "§e加载 {0} 时出错! 未知的触发方式: {1}");
        actionExecuteErrorSound = config.getString("action-execute-error-sound", "§e错误: 指定的音效 {0} 不存在");
        actionExecuteErrorGiveMoneyNotFoundEconomy = config.getString("action-execute-error-give-money-not-found-economy", "§e未找到经济插件, 无法执行 give-money 动作!");
        actionExecuteErrorGiveMoneyNotFoundEconomyTip = config.getString("action-execute-error-give-money-not-found-economy-tip", "§e请检查是否正确安装Vault插件以及经济提供插件! (如Essentials、CMI、Economy等)");
        actionExecuteErrorGiveMoneyInvalidValue = config.getString("action-execute-error-give-money-invalid-value", "错误: 无法执行 give-money 动作! 原因: 无效的数字格式: {0}");
        actionExecuteErrorGivePointsNotFoundPoints = config.getString("action-execute-error-give-points-not-found-points", "未找到点券插件, 无法执行 give-points 动作!");
        actionExecuteErrorGivePointsNotFoundPointsTip = config.getString("action-execute-error-give-points-not-found-points-tip", "请检查是否正确安装PlayerPoints插件!");
        actionExecuteErrorGivePointsInvalidValue = config.getString("action-execute-error-give-points-invalid-value", "错误: 无法执行 give-points 动作! 原因: 无效的数字格式: {0}");
        actionExecuteErrorTitleArgsError = config.getString("action-execute-error-title-args-error", "§e错误: 无法执行 title(-all) 动作! 原因: 无效的参数格式: {0}");
        actionExecuteErrorTitleNotSupport = config.getString("action-execute-error-title-not-support", "§e错误: 无法执行 title(-all) 动作! 原因: 当前服务端不支持此操作");
        actionExecuteErrorActionBarNotSupport = config.getString("action-execute-error-action-bar-not-support", "§e错误: 无法执行 action-bar(-all) 动作! 原因: 当前服务端不支持此操作");

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
