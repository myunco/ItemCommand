package ml.mcos.itemcommand.listener;

import ml.mcos.itemcommand.ItemCommand;
import ml.mcos.itemcommand.config.Config;
import ml.mcos.itemcommand.config.ItemInfo;
import ml.mcos.itemcommand.config.Language;
import ml.mcos.itemcommand.item.Item;
import ml.mcos.itemcommand.item.Trigger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;

public class PlayerInvolveEventListener implements Listener {
    private static final HashMap<String, HashMap<String, Long>> cdMap = new HashMap<>();
    private final int mcVersion = ItemCommand.getPlugin().getMcVersion();

    @EventHandler(priority = EventPriority.LOW)
    public void playerInteractEvent(PlayerInteractEvent event) {
        if ((mcVersion < 9 || event.getHand() == EquipmentSlot.HAND) && event.hasItem()) {
            ItemStack itemStack = event.getItem();
            Player player = event.getPlayer();
            boolean leftEvent = false;
            Item item;
            switch (event.getAction()) {
                case LEFT_CLICK_AIR:
                case LEFT_CLICK_BLOCK:
                    item = ItemInfo.matchItem(player, itemStack, Trigger.LEFT);
                    leftEvent = true;
                    break;
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:
                    item = ItemInfo.matchItem(player, itemStack, Trigger.RIGHT);
                    break;
                default:
                    item = null;
            }
            if (item != null && useItem(player, item, itemStack, player.getInventory().getHeldItemSlot())) {
                if (leftEvent) {
                    //event.setCancelled(Config.cancelLeftEvent); 这样可能会导致本来已经取消的事件被设为不取消
                    if (Config.cancelLeftEvent) {
                        event.setCancelled(true);
                    }
                } else {
                    if (Config.cancelRightEvent) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void playerItemHeldEvent(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItem(event.getNewSlot());
        if (itemStack != null) {
            Item item = ItemInfo.matchItem(player, itemStack, Trigger.HELD);
            if (item != null && useItem(player, item, itemStack, event.getNewSlot())) {
                event.setCancelled(Config.cancelHeldEvent);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void playerInventoryClickEvent(InventoryClickEvent event) {
        Inventory inventory = mcVersion < 8 ? event.getInventory() : event.getClickedInventory();
        if (mcVersion < 8 ? inventory instanceof CraftingInventory : inventory instanceof PlayerInventory) {
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                Player player = (Player) event.getWhoClicked();
                boolean leftEvent = false;
                Item item;
                switch (event.getClick()) {
                    case LEFT:
                    case SHIFT_LEFT:
                        item = ItemInfo.matchItem(player, itemStack, Trigger.INV_LEFT);
                        leftEvent = true;
                        break;
                    case RIGHT:
                    case SHIFT_RIGHT:
                        item = ItemInfo.matchItem(player, itemStack, Trigger.INV_RIGHT);
                        break;
                    default:
                        item = null;
                }
                if (item != null && useItem(player, item, itemStack, event.getSlot())) {
                    if (leftEvent) {
                        event.setCancelled(Config.cancelInvLeftEvent);
                    } else {
                        event.setCancelled(Config.cancelInvRightEvent);
                    }
                }
            }
        }
    }

    private static boolean useItem(Player player, Item item, ItemStack itemStack, int slot) {
        if (item.matchCondition(player) && item.hasPermission(player) && item.hasEnoughAmount(player, itemStack) && !isCooling(player, item.getId()) && item.charge(player)) {
            int cooldown = item.getCooldown(player);
            if (cooldown > 0) {
                putCooldown(player.getName(), item.getId(), cooldown);
            }
            int requiredAmount = item.getRequiredAmount(player);
            if (requiredAmount > 0) {
                if (itemStack.getAmount() > requiredAmount) {
                    itemStack.setAmount(itemStack.getAmount() - requiredAmount);
                } else if (itemStack.getAmount() == requiredAmount) {
                    player.getInventory().setItem(slot, null);
                } else {
                    ItemStack is = itemStack.clone();
                    is.setAmount(requiredAmount);
                    player.getInventory().removeItem(is);
                }
            }
            item.executeAction(player);
            return true;
        }
        return false;
    }

    private static void putCooldown(String player, String itemID, int cooldown) {
        cdMap.computeIfAbsent(player, k -> new HashMap<>()).put(itemID, System.currentTimeMillis() + cooldown * 1000L);
    }

    private static boolean isCooling(Player player, String itemID) {
        if (player.hasPermission("itemcommand.cooldown.bypass")) {
            return false;
        }
        HashMap<String, Long> map = cdMap.get(player.getName());
        if (map == null) {
            return false;
        }
        Long time = map.get(itemID);
        if (time == null) {
            return false;
        }
        long current = System.currentTimeMillis();
        if (time < current) {
            return false;
        }
        player.sendMessage(Language.replaceArgs(Language.useItemCooling, (int) ((time - current) / 1000)));
        return true;
    }

}
