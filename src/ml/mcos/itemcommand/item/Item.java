package ml.mcos.itemcommand.item;

import ml.mcos.itemcommand.ItemCommand;
import ml.mcos.itemcommand.action.Action;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Item {
    private static final Economy economy = ItemCommand.getPlugin().getEconomy();
    private static final PlayerPointsAPI pointsAPI = ItemCommand.getPlugin().getPointsAPI();

    private final String name;
    private final List<String> lore;
    private final Material type;
    private final Action[] action;
    private final double price;
    private final int points;
    private final int levels;
    private final String permission;
    private final int requiredAmount;
    private final int cooldown;

    public Item(String name, List<String> lore, Material type, Action[] action, double price, int points, int levels, String permission, int requiredAmount, int cooldown) {
        this.name = name;
        this.lore = lore;
        this.type = type;
        this.action = action;
        this.price = price;
        this.points = points;
        this.levels = levels;
        this.permission = permission;
        this.requiredAmount = requiredAmount;
        this.cooldown = cooldown;
    }

    public boolean match(ItemStack item) {
        if (type != null && type != item.getType()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (name != null && (meta == null || !name.equals(meta.getDisplayName()))) {
            return false;
        }
        return lore.isEmpty() || (meta != null && meta.hasLore() && lore.equals(meta.getLore()));
    }

    public void executeAction(Player player) {
        for (int i = 0; i < action.length; i++) {
            action[i].execute(player);
        }
    }

    public boolean charge(Player player) {
        if (price > 0.0) {
            if (economy == null) {
                player.sendMessage("§c错误: 未找到经济插件，无法扣除余额。");
            } else if (!economy.has(player, price)) {
                player.sendMessage("§c你没有足够的金钱(" + price + ")使用此物品。");
                return false;
            }
        }
        if (points > 0) {
            if (pointsAPI == null) {
                player.sendMessage("§c错误: 未找到点券插件，无法扣除点券。");
            } else if (pointsAPI.look(player.getUniqueId()) < points) {
                player.sendMessage("§c你没有足够的点券(" + points + ")使用此物品。");
                return false;
            }
        }
        if (levels > 0 && player.getLevel() < levels) {
            player.sendMessage("§c你没有足够的等级(" + levels + ")使用此物品。");
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
        if (permission == null || permission.isEmpty() || player.hasPermission(permission)) {
            return true;
        }
        player.sendMessage("§c你没有权限使用此物品。");
        return false;
    }

    public int getRequiredAmount() {
        return requiredAmount;
    }

    public boolean hasEnoughAmount(Player player, ItemStack item) {
        if (item.getAmount() >= requiredAmount) {
            return true;
        }
        if (requiredAmount < 1 || player.getInventory().containsAtLeast(item, requiredAmount)) {
            return true;
        }
        player.sendMessage("§c你没有足够数量的物品可以使用。");
        return false;
    }

    public int getCooldown() {
        return cooldown;
    }

}
