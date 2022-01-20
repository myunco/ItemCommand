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
        ItemMeta meta = item.getItemMeta();
        if (name != null && (meta == null || !name.equals(meta.getDisplayName()))) {
            return false;
        }
        if (!lore.isEmpty() && (meta == null || !lore.equals(meta.getLore()))) {
            return false;
        }
        return type == null || type == item.getType();
    }

    public void executeAction(Player player) {
        for (int i = 0; i < action.length; i++) {
            action[i].execute(player);
        }
    }

    public boolean charge(Player player) {
        if (price > 0.0 && !economy.has(player, price)) {
            player.sendMessage("§c你没有足够的金钱来使用此物品。");
            return false;
        }
        if (points > 0 && pointsAPI.look(player.getUniqueId()) < points) {
            player.sendMessage("§c你没有足够的点券来使用此物品。");
            return false;
        }
        if (levels > 0 && player.getLevel() < levels) {
            player.sendMessage("§c你没有足够的等级来使用此物品。");
            return false;
        }
        if (price > 0.0) {
            economy.withdrawPlayer(player, price);
        }
        if (points > 0) {
            pointsAPI.take(player.getUniqueId(), points);
        }
        if (levels > 0) {
            player.setLevel(player.getLevel() - levels);
        }
        return true;
    }

    public boolean hasPermission(Player player) {
        //return permission == null || player.hasPermission(permission);
        if (permission == null || player.hasPermission(permission)) {
            return true;
        }
        player.sendMessage("§c你没有权限使用此物品。");
        return false;
    }

    public int getRequiredAmount() {
        return requiredAmount;
    }

    public boolean meetRequiredAmount(Player player, ItemStack item) {
        if (item.getAmount() >= requiredAmount) {
            return true;
        }
        if (requiredAmount > 0 && player.getInventory().containsAtLeast(item, requiredAmount)) {
            player.sendMessage("§c你没有足够数量的物品用来使用。");
            return false;
        }
        return true;
    }

    public int getCooldown() {
        return cooldown;
    }

}
