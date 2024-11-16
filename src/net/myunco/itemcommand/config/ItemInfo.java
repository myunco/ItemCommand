package net.myunco.itemcommand.config;

import net.myunco.itemcommand.ItemCommand;
import net.myunco.itemcommand.action.Action;
import net.myunco.itemcommand.action.ActionBarAction;
import net.myunco.itemcommand.action.BroadcastAction;
import net.myunco.itemcommand.action.ChatAction;
import net.myunco.itemcommand.action.CommandAction;
import net.myunco.itemcommand.action.ConsoleAction;
import net.myunco.itemcommand.action.GiveMoneyAction;
import net.myunco.itemcommand.action.GivePointsAction;
import net.myunco.itemcommand.action.OperatorAction;
import net.myunco.itemcommand.action.ServerAction;
import net.myunco.itemcommand.action.SoundAction;
import net.myunco.itemcommand.action.TellAction;
import net.myunco.itemcommand.action.TitleAction;
import net.myunco.itemcommand.item.Item;
import net.myunco.itemcommand.item.Trigger;
import net.myunco.itemcommand.item.expression.Expression;
import net.myunco.itemcommand.item.expression.SimpleBooleanExpression;
import net.myunco.itemcommand.item.expression.SimpleDecimalExpression;
import net.myunco.itemcommand.item.expression.SimpleStringExpression;
import net.myunco.itemcommand.util.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ItemInfo {
    private static final ArrayList<Item> items = new ArrayList<>();
    public static final ArrayList<String> idList = new ArrayList<>();
    public static YamlConfiguration config;
    private static File itemsFile;
    private static final int mcVersion = ItemCommand.getPlugin().mcVersion;
    public static boolean heldEvent;
    public static boolean inventoryClickEvent;
    public static boolean entityDamageEvent;

    public static void loadItemInfo(ItemCommand plugin) {
        itemsFile = new File(plugin.getDataFolder(), "items.yml");
        if (!itemsFile.exists()) {
            plugin.saveResource("items.yml", true);
        }
        saveExampleConfig(plugin);
        config = Config.loadConfiguration(itemsFile);
        if (!items.isEmpty()) {
            items.clear();
            idList.clear();
        }
        for (String id : config.getKeys(false)) {
            Item item = loadItem(plugin, id);
            if (item != null) {
                items.add(item);
                idList.add(id);
            }
        }

        File itemsDir = new File(plugin.getDataFolder(), "items");
        if (!itemsDir.exists() && !itemsDir.mkdir() || !itemsDir.isDirectory()) {
            plugin.getLogger().severe("Unable to create items directory!");
        } else {
            YamlConfiguration itemConfig = config;
            for (File file : itemsDir.listFiles()) {
                config = Config.loadConfiguration(file);
                if (file.isFile() && file.getName().endsWith(".yml")) {
                    for (String id : config.getKeys(false)) {
                        if (idList.contains(id)) {
                            plugin.getLogger().warning("Duplicate item id: " + id + " in items/" + file.getName());
                            continue;
                        }
                        Item item = loadItem(plugin, id);
                        if (item != null) {
                            items.add(item);
                            idList.add(id);
                        }
                    }
                }
                // plugin.getLogger().info("Loaded " + items.size() + " items by " + file.getName());
            }
            config = itemConfig;
        }
        for (Item item : items) {
            if (!heldEvent && item.triggerContains(Trigger.HELD)) {
                heldEvent = true;
            }
            if (!inventoryClickEvent && item.triggerContains(Trigger.INV_LEFT) || item.triggerContains(Trigger.INV_RIGHT) || item.triggerContains(Trigger.INV_SHIFT_LEFT) || item.triggerContains(Trigger.INV_SHIFT_RIGHT)) {
                inventoryClickEvent = true;
            }
            if (!entityDamageEvent && item.triggerContains(Trigger.ARMOR_HIT) || item.triggerContains(Trigger.OFFHAND_HIT) || item.triggerContains(Trigger.HAND_HIT)) {
                entityDamageEvent = true;
            }
        }
        plugin.getLogger().info("Loaded " + items.size() + " items.");
    }

    private static void saveExampleConfig(ItemCommand plugin) {
        File file = new File(plugin.getDataFolder(), "示例物品配置-" + plugin.getDescription().getVersion() + ".yml");
        if (!file.exists()) {
            InputStream in = plugin.getResource("items.yml");
            if (in != null) {
                try {
                    OutputStream out = new FileOutputStream(file);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                    out.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Item loadItem(ItemCommand plugin, String id) {
        String name = config.getString(id + ".name");
        List<String> lore = config.getStringList(id + ".lore");
        String typeString = config.getString(id + ".type");
        if (name == null && lore.isEmpty() && typeString == null) {
            plugin.logMessage(Language.replaceArgs(Language.loadItemErrorNotMatch, id));
            return null;
        }
        String customModelData = null;
        if (mcVersion >= 14) {
            customModelData = config.getString(id + ".customModelData");
        }

        boolean loreExact = config.getBoolean(id + ".lore-exact", true);

        Material type = null;
        if (typeString != null) {
            try {
                type = Material.valueOf(typeString.toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.logMessage(Language.replaceArgs(Language.loadItemErrorUnknownType, id, typeString));
            }
        }

        List<String> conditionList = config.getStringList(id + ".condition");
        Expression[] condition = new Expression[conditionList.size()];
        for (int i = 0; i < condition.length; i++) {
            String conditionString = conditionList.get(i).trim();
            String conditionType = Utils.getTextLeft(conditionString, ':');
            if (conditionType.isEmpty()) {
                condition[i] = new SimpleBooleanExpression(conditionString);
            } else {
                String conditionValue = Utils.getTextRight(conditionString, ':').trim();
                switch (conditionType.toLowerCase()) {
                    case "s":
                        condition[i] = new SimpleStringExpression(conditionValue);
                        break;
                    case "d":
                        condition[i] = new SimpleDecimalExpression(conditionValue);
                        break;
                    default:
                        condition[i] = new SimpleBooleanExpression(conditionString);
                }
            }
        }

        List<String> triggerList = config.getStringList(id + ".trigger");
        if (triggerList.isEmpty()) {
            triggerList.add("right");
            triggerList.add("sneak_right");
        }
        Trigger[] trigger = new Trigger[triggerList.size()];
        for (int i = 0; i < trigger.length; i++) {
            try {
                trigger[i] = Trigger.valueOf(triggerList.get(i).toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.logMessage(Language.replaceArgs(Language.loadItemErrorUnknownTrigger, id, triggerList.get(i)));
            }
        }

        List<String> actionList = config.getStringList(id + ".action");
        Action[] action = new Action[actionList.size()];
        for (int i = 0; i < action.length; i++) {
            String actionString = actionList.get(i);
            String actionType = Utils.getTextLeft(actionString, ':');
            if (actionType.isEmpty()) {
                action[i] = new CommandAction(actionString);
            } else {
                String actionValue = Utils.getTextRight(actionString, ':').trim();
                switch (actionType.toLowerCase()) {
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
                    case "sound-all":
                        action[i] = new SoundAction(actionValue, true);
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
                    case "title":
                        action[i] = new TitleAction(actionValue);
                        break;
                    case "title-all":
                        action[i] = new TitleAction(actionValue, true);
                        break;
                    case "action-bar":
                        action[i] = new ActionBarAction(actionValue);
                        break;
                    case "action-bar-all":
                        action[i] = new ActionBarAction(actionValue, true);
                        break;
                    case "server":
                        action[i] = new ServerAction(actionValue);
                        break;
                    default:
                        action[i] = new CommandAction(actionString);
                }
            }
        }

        String price = config.getString(id + ".price");
        String points = config.getString(id + ".points");
        String levels = config.getString(id + ".levels");
        String permission = config.getString(id + ".permission");
        String requiredAmount = config.getString(id + ".required-amount");
        String cooldown = config.getString(id + ".cooldown");
        String cooldownMessage = config.getString(id + ".cooldown-message");
        boolean enchantment = config.getBoolean(id + ".enchantment");

        return new Item(id, name, lore, loreExact, type, customModelData, condition, trigger, action, price, points, levels, permission, requiredAmount, cooldown, cooldownMessage, enchantment);
    }

    public static Item matchItem(Player player, ItemStack item, Trigger trigger) {
        for (Item it : items) {
            if (it.match(player, item, trigger)) {
                return it;
            }
        }
        return null;
    }

    public static void saveConfig() {
        Config.saveConfiguration(config, itemsFile);
    }

    public static Item getItemById(String id) {
        for (Item it : items) {
            if (it.getId().equals(id)) {
                return it;
            }
        }
        return null;
    }

}
