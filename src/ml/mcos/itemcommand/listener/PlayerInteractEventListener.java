package ml.mcos.itemcommand.listener;

import ml.mcos.itemcommand.config.ItemInfo;
import ml.mcos.itemcommand.item.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class PlayerInteractEventListener implements Listener {
    private static final HashMap<String, HashMap<String, Long>> cdMap = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    public void playerInteractEvent(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND && event.hasItem()) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                ItemStack itemStack = event.getItem();
                Player player = event.getPlayer();
                Item item = ItemInfo.matchItem(player, itemStack);
                if (item != null) {
                    //noinspection ConstantConditions
                    if (item.hasPermission(player) && item.hasEnoughAmount(player, itemStack) && !isCooling(player, item.getId()) && item.charge(player)) {
                        int cooldown = item.getCooldown(player);
                        if (cooldown > 0) {
                            putCooldown(player.getName(), item.getId(), cooldown);
                        }
                        int requiredAmount = item.getRequiredAmount(player);
                        if (requiredAmount > 0) {
                            if (itemStack.getAmount() > requiredAmount) {
                                itemStack.setAmount(itemStack.getAmount() - requiredAmount);
                            } else if (itemStack.getAmount() == requiredAmount) {
                                player.getInventory().setItemInMainHand(null);
                            } else {
                                ItemStack stack = itemStack.clone();
                                stack.setAmount(requiredAmount);
                                player.getInventory().removeItem(stack);
                            }
                        }
                        item.executeAction(player);
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    private static void putCooldown(String player, String itemID, int cooldown) {
        cdMap.computeIfAbsent(player, k -> new HashMap<>()).put(itemID, System.currentTimeMillis() + cooldown * 1000L);
    }

    private static boolean isCooling(Player player, String itemID) {
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
        player.sendMessage("§4使用冷却: §c" + (int) ((time - current) / 1000) + "§4秒。");
        return true;
    }

}
