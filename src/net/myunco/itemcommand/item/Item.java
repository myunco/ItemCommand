package net.myunco.itemcommand.item;

import net.myunco.itemcommand.ItemCommand;
import net.myunco.itemcommand.action.Action;
import net.myunco.itemcommand.config.Language;
import net.myunco.itemcommand.item.expression.Expression;
import net.myunco.itemcommand.util.Utils;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private final ItemCommand plugin = ItemCommand.getPlugin();
    private final Economy economy = plugin.getEconomy();
    private final PlayerPointsAPI pointsAPI = plugin.getPointsAPI();

    private final String id;
    private final String name;
    private final List<String> lore;
    private final boolean loreExact;
    private final Material type;
    private final String customModelData;
    private final Expression[] condition;
    private final Trigger[] trigger;
    private final Action[] action;
    private final String price;
    private final String points;
    private final String levels;
    private final String permission;
    private final String requiredAmount;
    private final String cooldown;
    private final String cooldownMessage;
    private final boolean enchantment;

    public Item(String id, String name, List<String> lore, boolean loreExact, Material type, String customModelData, Expression[] condition, Trigger[] trigger, Action[] action,
                String price, String points, String levels, String permission, String requiredAmount, String cooldown, String cooldownMessage, boolean enchantment) {
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.loreExact = loreExact;
        this.type = type;
        this.customModelData = customModelData;
        this.condition = condition;
        this.trigger = trigger;
        this.action = action;
        this.price = price;
        this.points = points;
        this.levels = levels;
        this.permission = permission;
        this.requiredAmount = requiredAmount;
        this.cooldown = cooldown;
        this.cooldownMessage = cooldownMessage;
        this.enchantment = enchantment;
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

    public Material getType() {
        return type;
    }

    public int getCustomModelData() {
        int customModelData = this.customModelData == null ? 0 : Utils.parseInt(this.customModelData);
        if (customModelData == -1) {
            plugin.logMessage(Language.replaceArgs(Language.commandGiveErrorModel, id, this.customModelData));
        }
        return customModelData;
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

    public long getCooldown(Player player) {
        double cooldown = this.cooldown == null ? 0 : Utils.parseDouble(plugin.replacePlaceholders(player, this.cooldown));
        if (cooldown == -1) {
            plugin.logMessage(Language.replaceArgs(Language.useItemErrorCooldown, id, this.cooldown));
        }
        return (long) (cooldown * 1000);
    }

    public String getCooldownMessage(Player player) {
        return cooldownMessage == null ? null : plugin.replacePlaceholders(player, cooldownMessage);
    }

    public boolean isEnchantment() {
        return enchantment;
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
        return lore.isEmpty() || (meta != null && meta.hasLore() && compareLore(getLore(player), meta.getLore()));
    }

    private boolean compareLore(List<String> itemLore, List<String> metaLore) {
        if (loreExact) {
            return itemLore.equals(metaLore);
        }
        //noinspection SlowListContainsAll
        return metaLore.containsAll(itemLore);
    }

    public boolean matchCondition(Player player) {
        for (Expression expression : condition) {
            if (!expression.execute(player)) {
                expression.executeAction(player);
                return false;
            }
        }
        return true;
    }

    public boolean triggerContains(Trigger trigger) {
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
            action[i].call(player);
        }
    }

    public boolean charge(Player player) {
        double price = getPrice(player);
        boolean priceFree = player.hasPermission("itemcommand.price.free");
        if (price > 0.0 && !priceFree) {
            if (economy == null) {
                player.sendMessage(Language.useItemErrorNotEconomy);
            } else if (!economy.has(player, price)) {
                player.sendMessage(Language.replaceArgs(Language.useItemNotEnoughMoney, price));
                return false;
            }
        }
        int points = getPoints(player);
        boolean pointsFree = player.hasPermission("itemcommand.points.free");
        if (points > 0 && !pointsFree) {
            if (pointsAPI == null) {
                player.sendMessage(Language.useItemErrorNotPoints);
            } else if (pointsAPI.look(player.getUniqueId()) < points) {
                player.sendMessage(Language.replaceArgs(Language.useItemNotEnoughPoints, points));
                return false;
            }
        }
        int levels = getLevels(player);
        boolean levelsFree = player.hasPermission("itemcommand.levels.free");
        if (levels > 0 && !levelsFree && player.getLevel() < levels) {
            player.sendMessage(Language.replaceArgs(Language.useItemNotEnoughLevels, levels));
            return false;
        }
        if (price > 0.0 && !priceFree && economy != null) {
            economy.withdrawPlayer(player, price);
        }
        if (points > 0 && !pointsFree && pointsAPI != null) {
            pointsAPI.take(player.getUniqueId(), points);
        }
        if (levels > 0 && !levelsFree) {
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
