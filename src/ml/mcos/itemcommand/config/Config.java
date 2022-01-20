package ml.mcos.itemcommand.config;

import ml.mcos.itemcommand.ItemCommand;
import ml.mcos.itemcommand.action.Action;
import ml.mcos.itemcommand.action.BroadcastAction;
import ml.mcos.itemcommand.action.ChatAction;
import ml.mcos.itemcommand.action.CommandAction;
import ml.mcos.itemcommand.action.ConsoleAction;
import ml.mcos.itemcommand.action.GiveMoneyAction;
import ml.mcos.itemcommand.action.GivePointsAction;
import ml.mcos.itemcommand.action.OperatorAction;
import ml.mcos.itemcommand.action.SoundAction;
import ml.mcos.itemcommand.action.TellAction;
import ml.mcos.itemcommand.item.Item;
import ml.mcos.itemcommand.util.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Config {
    private static final ArrayList<Item> items = new ArrayList<>();
    public static final ArrayList<String> idList = new ArrayList<>();
    public static YamlConfiguration config;
    private static File configFile;

    public static void loadConfig(ItemCommand plugin) {
        plugin.saveDefaultConfig();
        configFile = new File(plugin.getDataFolder(), "config.yml");
        config = loadConfiguration(configFile);
        if (!items.isEmpty()) {
            items.clear();
        }
        if (!idList.isEmpty()) {
            idList.clear();
        }
        for (String id : config.getKeys(false)) {
            Item item = loadItem(plugin, id);
            if (item != null) {
                items.add(item);
                idList.add(id);
            }
        }
        plugin.getLogger().info("Loaded " + items.size() + " items.");
    }

    private static Item loadItem(ItemCommand plugin, String id) {
        String name = config.getString(id + ".name");
        List<String> lore = config.getStringList(id + ".lore");
        String typeString = config.getString(id + ".type");
        if (name == null && lore.isEmpty() && typeString == null) {
            plugin.getLogger().severe("加载 " + id + " 时出错! name、lore、type 至少需要提供一个, 当前全未提供!");
            return null;
        }

        Material type = null;
        if (typeString != null) {
            try {
                type = Material.valueOf(typeString);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("加载 " + id + " 时出错! 未知的物品类型: type: " + typeString);
            }
        }

        List<String> actionList = config.getStringList(id + ".action");
        Action[] action = new Action[actionList.size()];
        for (int i = 0; i < actionList.size(); i++) {
            String actionString = actionList.get(i);
            String actionType = Utils.getTextLeft(actionString, ':');
            if (actionType.isEmpty()) {
                action[i] = new CommandAction(actionString);
            } else {
                String actionValue = Utils.getTextRight(actionString, ':').trim();
                switch (actionType) {
                    case "cmd":
                        action[i] = new CommandAction(actionValue);
                        break;
                    case "op":
                        action[i] = new OperatorAction(actionValue);
                        break;
                    case "console":
                        action[i] = new ConsoleAction(actionValue);
                        break;
                    case "tell":
                        action[i] = new TellAction(actionValue);
                        break;
                    case "chat":
                        action[i] = new ChatAction(actionValue);
                        break;
                    case "sound":
                        action[i] = new SoundAction(actionValue);
                        break;
                    case "broadcast":
                        action[i] = new BroadcastAction(actionValue);
                        break;
                    case "give-money":
                        action[i] = new GiveMoneyAction(actionValue);
                        break;
                    case "give-points":
                        action[i] = new GivePointsAction(actionValue);
                        break;
                    default:
                        action[i] = new CommandAction(actionString);
                }
            }
        }

        String priceString = config.getString(id + ".price");
        double price = priceString == null ? 0.0 : Utils.parseDouble(priceString);
        if (price == -1.0) {
            plugin.getLogger().warning("加载 " + id + " 时出错! 无效的花费: price: " + priceString);
        }

        String pointsString = config.getString(id + ".points");
        int points = pointsString == null ? 0 : Utils.parseInt(pointsString);
        if (points == -1) {
            plugin.getLogger().warning("加载 " + id + " 时出错! 无效的花费: points: " + pointsString);
        }

        String levelsString = config.getString(id + ".levels");
        int levels = levelsString == null ? 0 : Utils.parseInt(levelsString);
        if (levels == -1) {
            plugin.getLogger().warning("加载 " + id + " 时出错! 无效的花费: levels: " + levelsString);
        }

        String permission = config.getString(id + ".permission");

        String requiredAmountString = config.getString(id + ".required-amount");
        int requiredAmount = requiredAmountString == null ? 0 : Utils.parseInt(requiredAmountString);
        if (requiredAmount == -1) {
            plugin.getLogger().warning("加载 " + id + " 时出错! 无效的需求数量: required-amount: " + requiredAmountString);
        }

        String cooldownString = config.getString(id + ".cooldown");
        int cooldown = cooldownString == null ? 0 : Utils.parseInt(cooldownString);
        if (cooldown == -1) {
            plugin.getLogger().warning("加载 " + id + " 时出错! 无效的冷却时间: cooldown: " + cooldownString);
        }

        return new Item(name, lore, type, action, price, points, levels, permission, requiredAmount, cooldown);
    }

    public static Item matchItem(ItemStack item) {
        for (Item it : items) {
            if (it.match(item)) {
                return it;
            }
        }
        return null;
    }

    public static void saveConfig() {
        saveConfiguration(config, configFile);
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
