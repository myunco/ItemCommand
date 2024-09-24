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
    public static String loadItemErrorNotFoundOperator;
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
    public static String actionExecuteErrorTitleInvalidTime;
    public static String actionExecuteErrorActionBarNotSupport;
    public static String actionParseErrorDelay;
    public static String actionParseErrorProbability;
    public static String actionParseErrorSeed;
    public static String useItemCooling;
    public static String useItemConditionInvalidNumber;
    public static String useItemErrorPrice;
    public static String useItemErrorPoints;
    public static String useItemErrorLevels;
    public static String useItemErrorRequiredAmount;
    public static String useItemErrorCooldown;
    public static String useItemErrorNotEconomy;
    public static String useItemErrorNotPoints;
    public static String useItemNotEnoughMoney;
    public static String useItemNotEnoughPoints;
    public static String useItemNotEnoughLevels;
    public static String useItemNotEnoughPermission;
    public static String useItemNotEnoughAmount;
    public static String commandList;
    public static String commandReload;
    public static String commandVersion;
    public static String commandUnknown;
    public static String commandAddNotItem;
    public static String commandAddNotName;
    public static String commandAddNotLore;
    public static String commandAddConsole;
    public static String commandAdd;
    public static String commandGiveUsage;
    public static String commandGiveTip1;
    public static String commandGiveTip2;
    public static String commandGiveNotFoundPlayer;
    public static String commandGiveNotFoundId;
    public static String commandGiveInvalidAmount;
    public static String commandGiveErrorAmount;
    public static String commandGiveErrorModel;
    public static String commandGiveInvalidType;
    public static String commandGive;
    public static String commandTypeNotItem;
    public static String commandTypeConsole;
    public static String commandType;

    public static void loadLanguage(String language) {
        if (language == null || !language.matches("[a-zA-Z]{2}[_-][a-zA-Z]{2}")) {
            plugin.getLogger().severe("§4语言文件名称格式错误: " + language);
            language = "zh_cn";
        }
        String langPath = "lang/" + language + ".yml";
        File lang = new File(plugin.getDataFolder(), langPath);
        saveDefaultLanguage(lang, langPath);
        YamlConfiguration config = Config.loadConfiguration(lang);
        version = config.getInt("version");
        logPrefix = config.getString("log-prefix", "[ItemCommand] ");
        messagePrefix = config.getString("message-prefix", "§8[§3ItemCommand§8] ");
        languageVersionError = config.getString("language-version-error", "§c语言文件版本错误: ");
        languageVersionOutdated = config.getString("language-version-outdated", "§e当前语言文件版本：§a{0} §c最新版本：§b{1} §6需要更新.");
        languageUpdateComplete = config.getString("language-update-complete", "§a语言文件更新完成!");
        configVersionError = config.getString("config-version-error", "§c配置文件版本错误: ");
        configVersionOutdated = config.getString("config-version-outdated", "§e当前配置文件版本：§a{0} §c最新版本：§b{1} §6需要更新.");
        configUpdateComplete = config.getString("config-update-complete", "§a配置文件更新完成!");
        languageUpdate(config, lang);
        updateFoundNewVersion = config.getString("update-found-new-version", "§c发现新版本可用! §b当前版本: {0} §d最新版本: {1}");
        updateMajorUpdate = config.getString("update-major-update", "§e(有大更新)");
        updateDownloadLink = config.getString("update-download-link", "§a下载地址: ");
        updateCheckFailure = config.getString("update-check-failure", "§e检查更新失败, 状态码: ");
        updateCheckException = config.getString("update-check-exception", "§4检查更新时发生IO异常.");
        loadItemErrorNotMatch = config.getString("load-item-error-not-match", "§e加载 {0} 时出错! name、lore、type 至少需要提供一个, 当前全未提供");
        loadItemErrorUnknownType = config.getString("load-item-error-unknown-type", "§e加载 {0} 时出错! 未知的物品类型: type: {1}");
        loadItemErrorNotFoundOperator = config.getString("load-item-error-not-found-operator", "§e加载条件时出错! 在条件表达式中未找到运算符: {0}");
        loadItemErrorUnknownTrigger = config.getString("load-item-error-unknown-trigger", "§e加载 {0} 时出错! 未知的触发方式: {1}");
        actionExecuteErrorSound = config.getString("action-execute-error-sound", "§e错误: 无法执行 sound(-all) 动作! 原因: 指定的音效 {0} 不存在");
        actionExecuteErrorGiveMoneyNotFoundEconomy = config.getString("action-execute-error-give-money-not-found-economy", "§e未找到经济插件, 无法执行 give-money 动作!");
        actionExecuteErrorGiveMoneyNotFoundEconomyTip = config.getString("action-execute-error-give-money-not-found-economy-tip", "§e请检查是否正确安装Vault插件以及经济提供插件! (如Essentials、CMI、Economy等)");
        actionExecuteErrorGiveMoneyInvalidValue = config.getString("action-execute-error-give-money-invalid-value", "§e错误: 无法执行 give-money 动作! 原因: 无效的数字格式: {0}");
        actionExecuteErrorGivePointsNotFoundPoints = config.getString("action-execute-error-give-points-not-found-points", "§e未找到点券插件, 无法执行 give-points 动作!");
        actionExecuteErrorGivePointsNotFoundPointsTip = config.getString("action-execute-error-give-points-not-found-points-tip", "§e请检查是否正确安装PlayerPoints插件!");
        actionExecuteErrorGivePointsInvalidValue = config.getString("action-execute-error-give-points-invalid-value", "§e错误: 无法执行 give-points 动作! 原因: 无效的数字格式: {0}");
        actionExecuteErrorTitleArgsError = config.getString("action-execute-error-title-args-error", "§e错误: 无法执行 title(-all) 动作! 原因: 无效的参数格式: {0}");
        actionExecuteErrorTitleNotSupport = config.getString("action-execute-error-title-not-support", "§e错误: 无法执行 title(-all) 动作! 原因: 当前服务端不支持此操作");
        actionExecuteErrorTitleInvalidTime = config.getString("action-execute-error-title-invalid-time", "§e错误: 无法执行 title(-all) 动作! 原因: 无效的显示时间: {0}");
        actionExecuteErrorActionBarNotSupport = config.getString("action-execute-error-action-bar-not-support", "§e错误: 无法执行 action-bar(-all) 动作! 原因: 当前服务端不支持此操作");
        actionParseErrorDelay = config.getString("action-parse-error-delay", "§e解析动作时出错! 在延时中发现无效数字: {0}");
        actionParseErrorProbability = config.getString("action-parse-error-probability", "§e解析动作时出错! 在概率中发现无效数字: {0}");
        actionParseErrorSeed = config.getString("action-parse-error-seed", "§e解析动作时出错! 在种子中发现无效数字: {0}");
        useItemCooling = config.getString("use-item-cooling", "§4使用冷却: §c{0}§4秒。");
        useItemConditionInvalidNumber = config.getString("use-item-condition-invalid-number", "§e解析条件时出错! 在数值比较表达式中发现无效数字: {0}");
        useItemErrorPrice = config.getString("use-item-error-price", "§e解析 {0} 时出错! 无效的花费: price: {1}");
        useItemErrorPoints = config.getString("use-item-error-points", "§e解析 {0} 时出错! 无效的花费: points: {1}");
        useItemErrorLevels = config.getString("use-item-error-levels", "§e解析 {0} 时出错! 无效的花费: levels: {1}");
        useItemErrorRequiredAmount = config.getString("use-item-error-required-amount", "§e解析 {0} 时出错! 无效的需求数量: required-amount: {1}");
        useItemErrorCooldown = config.getString("use-item-error-cooldown", "§e解析 {0} 时出错! 无效的冷却时间: cooldown: {1}");
        useItemErrorNotEconomy = config.getString("use-item-error-not-economy", "§c错误: 未找到经济插件，无法扣除余额。");
        useItemErrorNotPoints = config.getString("use-item-error-not-points", "§c错误: 未找到点券插件，无法扣除点券。");
        useItemNotEnoughMoney = config.getString("use-item-not-enough-money", "§c你没有足够的金钱({0})使用此物品。");
        useItemNotEnoughPoints = config.getString("use-item-not-enough-points", "§c你没有足够的点券({0})使用此物品。");
        useItemNotEnoughLevels = config.getString("use-item-not-enough-levels", "§c你没有足够的等级({0})使用此物品。");
        useItemNotEnoughPermission = config.getString("use-item-not-enough-permission", "§c你没有权限使用此物品。");
        useItemNotEnoughAmount = config.getString("use-item-not-enough-amount", "§c你没有足够数量的物品可以使用。 (需要{0}个)");
        commandList = config.getString("command-list", "§6已加载的物品ID列表: §a{0}");
        commandReload = config.getString("command-reload", "§a配置文件重载完成。");
        commandVersion = config.getString("command-version", "§a当前版本: §b{0}");
        commandUnknown = config.getString("command-unknown", "§6未知的子命令");
        commandAddNotItem = config.getString("command-add-not-item", "§d你确定你手里有物品？");
        commandAddNotName = config.getString("command-add-not-name", "§a你手中的物品没有显示名称, 无法添加name项。");
        commandAddNotLore = config.getString("command-add-not-lore", "§a你手中的物品没有Lore, 无法添加lore项。");
        commandAddConsole = config.getString("command-add-console", "§a控制台无法使用此命令。");
        commandAdd = config.getString("command-add", "§a已添加到配置文件, ID为: {0}, 快去修改吧!");
        commandGiveUsage = config.getString("command-give-usage", "§6用法: /ic give <玩家> <物品ID> <物品数量> [物品类型]");
        commandGiveTip1 = config.getString("command-give-tip1", "§7(物品类型参数仅在指定ID的物品配置中未指定物品类型时需要, 默认为石头)");
        commandGiveTip2 = config.getString("command-give-tip2", "§b<> = 必填参数 [] = 可选参数");
        commandGiveNotFoundPlayer = config.getString("command-give-not-found-player", "§c指定的玩家不在线或不存在！");
        commandGiveNotFoundId = config.getString("command-give-not-found-id", "§c指定的ID不存在或未能正确加载。");
        commandGiveInvalidAmount = config.getString("command-give-invalid-amount", "§c参数错误: 无效的数量: {0}");
        commandGiveErrorAmount = config.getString("command-give-error-amount", "§c错误: 物品数量不能小于1");
        commandGiveErrorModel = config.getString("command-give-error-model", "§e解析 {0} 时出错! 无效的自定义模型数据: customModelData: {1}");
        commandGiveInvalidType = config.getString("command-give-invalid-type", "§c错误: 无效的物品类型: {0}");
        commandGive = config.getString("command-give", "§a已将§b{0}§a个{1}§a添加到§c{2}§a的物品栏.");
        commandTypeNotItem = config.getString("command-type-not-item", "§d你确定你手里有物品？");
        commandTypeConsole = config.getString("command-type-console", "§a控制台无法使用此命令。");
        commandType = config.getString("command-type", "§a当前手持物品的类型是: §b{0}");
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
        int latestVersion = 3;
        if (version < latestVersion) {
            plugin.logMessage(replaceArgs(languageVersionOutdated, version, latestVersion));
            switch (version) {
                case 1:
                    config.set("command-type-not-item", "§d你确定你手里有物品？");
                    config.set("command-type-console", "§a控制台无法使用此命令。");
                    config.set("command-type", "§a当前手持物品的类型是: §b{0}");
                case 2:
                    config.set("command-give-error-model", "§e解析 {0} 时出错! 无效的自定义模型数据: customModelData: {1}");
                    config.set("action-parse-error-delay", "§e解析动作时出错! 在延时中发现无效数字: {0}");
                    config.set("action-parse-error-probability", "§e解析动作时出错! 在概率中发现无效数字: {0}");
                    config.set("action-parse-error-seed", "§e解析动作时出错! 在种子中发现无效数字: {0}");
                    break;
                default:
                    plugin.logMessage(languageVersionError + version);
                    return;
            }
            plugin.logMessage(languageUpdateComplete);
            version = latestVersion;
            config.set("version", latestVersion);
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
