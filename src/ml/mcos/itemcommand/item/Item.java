package ml.mcos.itemcommand.item;

import ml.mcos.itemcommand.ItemCommand;
import ml.mcos.itemcommand.action.Action;
import ml.mcos.itemcommand.config.Language;
import ml.mcos.itemcommand.item.expression.Expression;
import ml.mcos.itemcommand.util.Utils;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private static final ItemCommand plugin = ItemCommand.getPlugin();
    private final Economy economy = plugin.getEconomy();
    private final PlayerPointsAPI pointsAPI = plugin.getPointsAPI();

    private final String id;
    private final String name;
    private final List<String> lore;
    private final Material type;
    private final Expression[] condition;
    private final Trigger[] trigger;
    private final Action[] action;
    private final String price;
    private final String points;
    private final String levels;
    private final String permission;
    private final String requiredAmount;
    private final String cooldown;

    public Item(String id, String name, List<String> lore, Material type, Expression[] condition, Trigger[] trigger, Action[] action, String price, String points, String levels, String permission, String requiredAmount, String cooldown) {
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.type = type;
        this.condition = condition;
        this.trigger = trigger;
        this.action = action;
        this.price = price;
        this.points = points;
        this.levels = levels;
        this.permission = permission;
        this.requiredAmount = requiredAmount;
        this.cooldown = cooldown;
    }

    public String getId() {
        return id;
    }

    public String getName(Player player) {
        return plugin.replacePlaceholders(player, name);
    }

    public List<String> getLore(Player player) {
        for (String s : lore) {
            if (ItemCommand.mayContainPlaceholders(s) || s.indexOf('{') != -1) {
                ArrayList<String> resultLore = new ArrayList<>(lore.size());
                for (String s1 : lore) {
                    resultLore.add(plugin.replacePlaceholders(player, s1));
                }
                return resultLore;
            }
        }
        return lore;
    }

    public double getPrice(Player player) {
        double price = this.price == null ? 0.0 : Utils.parseDouble(plugin.replacePlaceholders(player, this.price));
        if (price == -1.0) {
            plugin.logMessage(Language.replaceArgs(Language.useItemErrorPrice, id, this.price));
        }
        return price;
    }

    public int getPoints(Player player) {
        int points = this.points == null ? 0 : Utils.parseInt(plugin.replacePlaceholders(player, this.points));
        if (points == -1) {
            plugin.logMessage(Language.replaceArgs(Language.useItemErrorPoints, id, this.points));
        }
        return points;
    }

    public int getLevels(Player player) {
        int levels = this.levels == null ? 0 : Utils.parseInt(plugin.replacePlaceholders(player, this.levels));
        if (levels == -1) {
            plugin.logMessage(Language.replaceArgs(Language.useItemErrorLevels, id, this.levels));
        }
        return levels;
    }

    public String getPermission(Player player) {
        return plugin.replacePlaceholders(player, permission);
    }

    public int getRequiredAmount(Player player) {
        int requiredAmount = this.requiredAmount == null ? 0 : Utils.parseInt(plugin.replacePlaceholders(player, this.requiredAmount));
        if (requiredAmount == -1) {
            plugin.logMessage(Language.replaceArgs(Language.useItemErrorRequiredAmount, id, this.requiredAmount));
        }
        return requiredAmount;
    }

    public int getCooldown(Player player) {
        int cooldown = this.cooldown == null ? 0 : Utils.parseInt(plugin.replacePlaceholders(player, this.cooldown));
        if (cooldown == -1) {
            plugin.logMessage(Language.replaceArgs(Language.useItemErrorCooldown, id, this.cooldown));
        }
        return cooldown;
    }

    public boolean match(Player player, ItemStack item, Trigger trigger) {
        if (!triggerContains(trigger)) {
            return false;
        }
        if (type != null && type != item.getType()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (name != null && (meta == null || !getName(player).equals(meta.getDisplayName()))) {
            return false;
        }
        return lore.isEmpty() || (meta != null && meta.hasLore() && getLore(player).equals(meta.getLore()));
    }

    public boolean matchCondition(Player player) {
        for (Expression expression : condition) {
            if (!expression.execute(player)) {
                String msg = expression.getMessage(player);
                if (msg != null && !msg.isEmpty()) {
                    player.sendMessage(msg);
                }
                return false;
            }
        }
        return true;
    }

    private boolean triggerContains(Trigger trigger) {
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < this.trigger.length; i++) {
            if (this.trigger[i] == trigger) {
                return true;
            }
        }
        return false;
    }

    public void executeAction(Player player) {
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < action.length; i++) {
            action[i].execute(player);
        }
    }

    public boolean charge(Player player) {
        double price = getPrice(player);
        if (price > 0.0) {
            if (economy == null) {
                player.sendMessage(Language.useItemErrorNotEconomy);
            } else if (!economy.has(player, price)) {
                player.sendMessage(Language.replaceArgs(Language.useItemNotEnoughMoney, price));
                return false;
            }
        }
        int points = getPoints(player);
        if (points > 0) {
            if (pointsAPI == null) {
                player.sendMessage(Language.useItemErrorNotPoints);
            } else if (pointsAPI.look(player.getUniqueId()) < points) {
                player.sendMessage(Language.replaceArgs(Language.useItemNotEnoughPoints, points));
                return false;
            }
        }
        int levels = getLevels(player);
        if (levels > 0 && player.getLevel() < levels) {
            player.sendMessage(Language.replaceArgs(Language.useItemNotEnoughLevels, levels));
            return false;
        }
        if (price > 0.0 && economy != null) {
            economy.withdrawPlayer(player, price);
        }
        if (points > 0 && pointsAPI != null) {
            pointsAPI.take(player.getUniqueId(), points);
        }
        if (levels > 0) {
            player.setLevel(player.getLevel() - levels);
        }
        return true;
    }

    public boolean hasPermission(Player player) {
        String permission = getPermission(player);
        if (permission == null || permission.isEmpty() || player.hasPermission(permission)) {
            return true;
        }
        player.sendMessage(Language.useItemNotEnoughPermission);
        return false;
    }

    public boolean hasEnoughAmount(Player player, ItemStack item) {
        int requiredAmount = getRequiredAmount(player);
        if (item.getAmount() >= requiredAmount) {
            return true;
        }
        if (requiredAmount < 1 || player.getInventory().containsAtLeast(item, requiredAmount)) {
            return true;
        }
        player.sendMessage(Language.replaceArgs(Language.useItemNotEnoughAmount, requiredAmount));
        return false;
    }

}
