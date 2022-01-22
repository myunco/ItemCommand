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
    private static final HashMap<String, Long> cdMap = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void playerInteractEvent(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND && event.hasItem()) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                ItemStack itemStack = event.getItem();
                Item item = ItemInfo.matchItem(itemStack);
                if (item != null) {
                    Player player = event.getPlayer();
                    //noinspection ConstantConditions
                    if (item.hasPermission(player) && item.hasEnoughAmount(player, itemStack) && !isCooling(player) && item.charge(player)) {
                        if (item.getCooldown() > 0) {
                            cdMap.put(player.getName(), System.currentTimeMillis() + item.getCooldown() * 1000L);
                        }
                        int requiredAmount = item.getRequiredAmount();
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

    private static boolean isCooling(Player player) {
        Long time = cdMap.get(player.getName());
        if (time == null) {
            return false;
        }
        long current = System.currentTimeMillis();
        if (time >= current) {
            player.sendMessage("§4使用冷却: §c" + (int) ((time - current) / 1000) + "§4秒。");
            return true;
        }
        return false;
    }

}
