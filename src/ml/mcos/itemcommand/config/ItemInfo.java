package ml.mcos.itemcommand.config;

import ml.mcos.itemcommand.ItemCommand;
import ml.mcos.itemcommand.action.Action;
import ml.mcos.itemcommand.action.ActionBarAction;
import ml.mcos.itemcommand.action.BroadcastAction;
import ml.mcos.itemcommand.action.ChatAction;
import ml.mcos.itemcommand.action.CommandAction;
import ml.mcos.itemcommand.action.ConsoleAction;
import ml.mcos.itemcommand.action.GiveMoneyAction;
import ml.mcos.itemcommand.action.GivePointsAction;
import ml.mcos.itemcommand.action.OperatorAction;
import ml.mcos.itemcommand.action.ServerAction;
import ml.mcos.itemcommand.action.SoundAction;
import ml.mcos.itemcommand.action.TellAction;
import ml.mcos.itemcommand.action.TitleAction;
import ml.mcos.itemcommand.item.Item;
import ml.mcos.itemcommand.item.Trigger;
import ml.mcos.itemcommand.item.expression.Expression;
import ml.mcos.itemcommand.item.expression.SimpleBooleanExpression;
import ml.mcos.itemcommand.item.expression.SimpleDecimalExpression;
import ml.mcos.itemcommand.item.expression.SimpleStringExpression;
import ml.mcos.itemcommand.util.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemInfo {
    private static final ArrayList<Item> items = new ArrayList<>();
    public static final ArrayList<String> idList = new ArrayList<>();
    public static YamlConfiguration config;
    private static File itemsFile;

    public static void loadItemInfo(ItemCommand plugin) {
        itemsFile = new File(plugin.getDataFolder(), "items.yml");
        if (!itemsFile.exists()) {
            plugin.saveResource("items.yml", true);
        }
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
        plugin.getLogger().info("Loaded " + items.size() + " items.");
    }

    private static Item loadItem(ItemCommand plugin, String id) {
        String name = config.getString(id + ".name");
        List<String> lore = config.getStringList(id + ".lore");
        String typeString = config.getString(id + ".type");
        if (name == null && lore.isEmpty() && typeString == null) {
            plugin.logMessage(Language.replaceArgs(Language.loadItemErrorNotMatch, id));
            return null;
        }

        Material type = null;
        if (typeString != null) {
            try {
                type = Material.valueOf(typeString);
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

        return new Item(id, name, lore, type, condition, trigger, action, price, points, levels, permission, requiredAmount, cooldown);
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

}
