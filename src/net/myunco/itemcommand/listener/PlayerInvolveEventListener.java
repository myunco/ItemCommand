package net.myunco.itemcommand.listener;

import net.myunco.itemcommand.ItemCommand;
import net.myunco.itemcommand.config.Config;
import net.myunco.itemcommand.config.CooldownInfo;
import net.myunco.itemcommand.config.ItemInfo;
import net.myunco.itemcommand.config.Language;
import net.myunco.itemcommand.item.Item;
import net.myunco.itemcommand.item.Trigger;
import net.myunco.itemcommand.util.Version;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.UUID;

public class PlayerInvolveEventListener implements Listener {
    public static final HashMap<UUID, HashMap<String, Long>> cdMap = new HashMap<>();
    private final Version mcVersion = ItemCommand.getPlugin().mcVersion;

    @EventHandler(priority = EventPriority.LOW)
    public void playerInteractEvent(PlayerInteractEvent event) {
        if (event.hasItem() && event.getAction() != Action.PHYSICAL) {
            ItemStack itemStack = event.getItem(); //不会是null 不会是空气
            assert itemStack != null;
            Player player = event.getPlayer();
            boolean leftClick = false;
            boolean mainHand = mcVersion.isLessThan(9) || event.getHand() == EquipmentSlot.HAND;
            Item item;
            switch (event.getAction()) {
                case LEFT_CLICK_AIR:
                case LEFT_CLICK_BLOCK:
                    if (mainHand) {
                        item = ItemInfo.matchItem(player, itemStack, player.isSneaking() ? Trigger.SNEAK_LEFT : Trigger.LEFT);
                    } else {
                        item = ItemInfo.matchItem(player, itemStack, player.isSneaking() ? Trigger.SNEAK_OFFHAND_LEFT : Trigger.OFFHAND_LEFT);
                    }
                    leftClick = true;
                    break;
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:
                    if (mainHand) {
                        item = ItemInfo.matchItem(player, itemStack, player.isSneaking() ? Trigger.SNEAK_RIGHT : Trigger.RIGHT);
                    } else {
                        item = ItemInfo.matchItem(player, itemStack, player.isSneaking() ? Trigger.SNEAK_OFFHAND_RIGHT : Trigger.OFFHAND_RIGHT);

                    }
                    break;
                default:
                    // 不可能执行的代码
                    item = null;
            }
            if (item != null && useItem(player, item, itemStack, mainHand ? player.getInventory().getHeldItemSlot() : 40)) {
                if (leftClick) {
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
        if (ItemInfo.heldEvent) {
            Player player = event.getPlayer();
            ItemStack itemStack = player.getInventory().getItem(event.getNewSlot());
            if (itemStack != null) { //不会是空气
                Item item = ItemInfo.matchItem(player, itemStack, Trigger.HELD);
                if (item != null && useItem(player, item, itemStack, event.getNewSlot())) {
                    if (Config.cancelHeldEvent) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void playerInventoryClickEvent(InventoryClickEvent event) {
        if (!ItemInfo.inventoryClickEvent) {
            return;
        }
        Inventory inventory = mcVersion.isLessThan(8) ? event.getInventory() : event.getClickedInventory();
        if (mcVersion.isLessThan(8) ? inventory instanceof CraftingInventory : inventory instanceof PlayerInventory) {
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                Player player = (Player) event.getWhoClicked();
                boolean leftEvent = false;
                Item item;
                switch (event.getClick()) {
                    case LEFT:
                        item = ItemInfo.matchItem(player, itemStack, Trigger.INV_LEFT);
                        leftEvent = true;
                        break;
                    case SHIFT_LEFT:
                        item = ItemInfo.matchItem(player, itemStack, Trigger.INV_SHIFT_LEFT);
                        leftEvent = true;
                        break;
                    case RIGHT:
                        item = ItemInfo.matchItem(player, itemStack, Trigger.INV_RIGHT);
                        break;
                    case SHIFT_RIGHT:
                        item = ItemInfo.matchItem(player, itemStack, Trigger.INV_SHIFT_RIGHT);
                        break;
                    default:
                        item = null;
                }
                if (item != null && useItem(player, item, itemStack, event.getSlot())) {
                    if (leftEvent) {
                        if (Config.cancelInvLeftEvent) {
                            event.setCancelled(true);
                        }
                    } else {
                        if (Config.cancelInvRightEvent) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!ItemInfo.entityDamageEvent) {
            return;
        }
        if (event.getEntityType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();
            ItemStack stack = player.getInventory().getItemInHand();
            if (stack.getType() != Material.AIR) {
                Item item = ItemInfo.matchItem(player, stack, Trigger.HAND_HIT);
                if (item != null) {
                    useItem(player, item, stack, player.getInventory().getHeldItemSlot());
                }
            }
            if (mcVersion.isGreaterThanOrEqualTo(9)) {
                stack = player.getInventory().getItemInOffHand();
                if (stack.getType() != Material.AIR) {
                    Item item = ItemInfo.matchItem(player, stack, Trigger.OFFHAND_HIT);
                    if (item != null) {
                        useItem(player, item, stack, 40);
                    }
                }
            }
            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    Item item = ItemInfo.matchItem(player, itemStack, Trigger.ARMOR_HIT);
                    if (item != null) {
                        useItem(player, item, itemStack, -1);
                    }
                }
            }
        }
    }

    private static boolean useItem(Player player, Item item, ItemStack itemStack, int slot) {
        if (item.hasPermission(player) && !isCooling(player, item.getCooldownId(), item.getCooldownMessage(player)) && item.hasEnoughAmount(player, itemStack) && item.matchCondition(player) && item.charge(player)) {
            long cooldown = item.getCooldown(player);
            if (cooldown > 0) {
                putCooldown(player.getUniqueId(), item.getCooldownId(), cooldown);
            }
            int requiredAmount = item.getRequiredAmount(player);
            if (requiredAmount > 0) {
                if (itemStack.getAmount() > requiredAmount) {
                    itemStack.setAmount(itemStack.getAmount() - requiredAmount);
                } else if (itemStack.getAmount() == requiredAmount) {
                    if (slot == -1) {
                        player.getInventory().removeItem(itemStack);
                    } else {
                        player.getInventory().setItem(slot, null);
                    }
                } else {
                    ItemStack is = itemStack.clone();
                    is.setAmount(requiredAmount);
                    player.getInventory().removeItem(is);
                }
            }
            item.executeAction(player);
        }
        return true;
    }

    private static void putCooldown(UUID player, String cooldownId, long cooldown) {
        long cdEndTime = System.currentTimeMillis() + cooldown;
        cdMap.computeIfAbsent(player, k -> new HashMap<>()).put(cooldownId, cdEndTime);
        if (cooldown > 300000) { // 5分钟以上才持久化保存 减少资源消耗
            CooldownInfo.putCooldownInfo(player.toString(), cooldownId, cdEndTime);
        }
    }

    private static boolean isCooling(Player player, String cooldownId, String cooldownMessage) {
        if (player.hasPermission("itemcommand.cooldown.bypass")) {
            return false;
        }
        HashMap<String, Long> map = cdMap.get(player.getUniqueId());
        if (map == null) {
            return false;
        }
        Long time = map.get(cooldownId);
        if (time == null) {
            return false;
        }
        long current = System.currentTimeMillis();
        if (time < current) {
            return false;
        }
        if (cooldownMessage == null || cooldownMessage.isEmpty()) {
            cooldownMessage = Language.useItemCooling;
        } else if (cooldownMessage.equals("none")) {
            return true;
        }
        player.sendMessage(Language.replaceArgs(cooldownMessage, String.format("%.2f", (time - current) / 1000d)));
        return true;
    }

}
