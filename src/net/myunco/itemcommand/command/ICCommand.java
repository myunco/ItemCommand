package net.myunco.itemcommand.command;

import net.myunco.itemcommand.ItemCommand;
import net.myunco.itemcommand.config.ItemInfo;
import net.myunco.itemcommand.config.Language;
import net.myunco.itemcommand.item.Item;
import net.myunco.itemcommand.update.UpdateChecker;
import net.myunco.itemcommand.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ICCommand implements TabExecutor {
    private final ItemCommand plugin;

    public ICCommand(ItemCommand plugin) {
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        switch (args[0].toLowerCase()) {
            case "add":
                commandAdd(sender, args);
                break;
            case "give":
                commandGive(sender, args);
                break;
            case "list":
                sendMessage(sender, Language.replaceArgs(Language.commandList, String.join(", ", ItemInfo.idList)));
                break;
            case "reload":
                UpdateChecker.stop();
                plugin.init();
                sendMessage(sender, Language.commandReload);
                break;
            case "type":
                commandType(sender);
                break;
            case "version":
                sendMessage(sender, Language.replaceArgs(Language.commandVersion, plugin.getDescription().getVersion()));
                break;
            default:
                sendMessage(sender, Language.commandUnknown);
        }
        return true;
    }

    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(Language.messagePrefix + message);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args[0].equalsIgnoreCase("give")) {
            switch (args.length) {
                case 3:
                    return TabComplete.getCompleteList(args, ItemInfo.idList, true);
                case 4:
                    return TabComplete.getCompleteList(args, TabComplete.amountList);
                case 5:
                    return TabComplete.getCompleteList(args, TabComplete.typeList, true);
            }
        }
        return TabComplete.getCompleteList(args, TabComplete.getTabList(args, command.getName()));
    }

    @SuppressWarnings("deprecation")
    private void commandAdd(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            ItemStack item = plugin.mcVersion > 8 ? ((Player) sender).getInventory().getItemInMainHand() : ((Player) sender).getItemInHand();
            if (item.getType() == Material.AIR) {
                sendMessage(sender, Language.commandAddNotItem);
                return;
            }
            ItemMeta meta = item.getItemMeta();
            assert meta != null; //只有Material.AIR的meta是null
            String id;
            if (args.length > 1) {
                if (args[1].indexOf('.') != -1) {
                    sendMessage(sender, Language.commandAddInvalidId);
                    return;
                } else if (ItemInfo.idList.contains(args[1])) {
                    sendMessage(sender, Language.commandAddIdExist);
                    return;
                }
                id = args[1];
            } else {
                id = String.valueOf(System.currentTimeMillis());
            }
            if (!meta.hasDisplayName()) {
                sendMessage(sender, Language.commandAddNotName);
            } else {
                ItemInfo.config.set(id + ".name", meta.getDisplayName());
            }
            if (!meta.hasLore()) {
                sendMessage(sender, Language.commandAddNotLore);
            } else {
                ItemInfo.config.set(id + ".lore", meta.getLore());
            }
            ItemInfo.config.set(id + ".lore-exact", true);
            ItemInfo.config.set(id + ".type", item.getType().toString());
            if (plugin.mcVersion >= 14) {
                ItemInfo.config.set(id + ".custom-model-data", meta.getCustomModelData());
            }
            ItemInfo.config.set(id + ".condition", Collections.singletonList("true"));
            ItemInfo.config.set(id + ".trigger", Collections.singletonList("right"));
            ItemInfo.config.set(id + ".action", Collections.singletonList("cmd: help"));
            ItemInfo.config.set(id + ".price", 0);
            ItemInfo.config.set(id + ".points", 0);
            ItemInfo.config.set(id + ".levels", 0);
            ItemInfo.config.set(id + ".permission", "");
            ItemInfo.config.set(id + ".required-amount", 1);
            ItemInfo.config.set(id + ".cooldown", 0);
            ItemInfo.config.set(id + ".cooldown-message", "");
            ItemInfo.saveConfig();
            sendMessage(sender, Language.replaceArgs(Language.commandAdd, id));
            return;
        }
        sendMessage(sender, Language.commandAddConsole);
    }

    @SuppressWarnings("deprecation")
    private void commandGive(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendMessage(sender, Language.commandGiveUsage);
            sendMessage(sender, Language.commandGiveTip1);
            sendMessage(sender, Language.commandGiveTip2);
            return;
        }
        Player player = plugin.getServer().getPlayer(args[1]);
        if (player == null) {
            sendMessage(sender, Language.commandGiveNotFoundPlayer);
        } else if (!ItemInfo.idList.contains(args[2])) {
            sendMessage(sender, Language.commandGiveNotFoundId);
        } else {
            int amount = args.length == 3 ? 1 : Utils.parseInt(args[3]);
            if (amount == -1) {
                sendMessage(sender, Language.replaceArgs(Language.commandGiveInvalidAmount, args[3]));
                return;
            } else if (amount == 0) {
                sendMessage(sender, Language.commandGiveErrorAmount);
                return;
            }
            Item it = ItemInfo.getItemById(args[2]);
            String name = it.getName(player);
            List<String> lore = it.getLore(player);
            Material type = it.getType();
            if (type == null) {
                try {
                    type = args.length <= 4 ? Material.STONE : Material.valueOf(args[4].toUpperCase());
                } catch (IllegalArgumentException e) {
                    sendMessage(sender, Language.replaceArgs(Language.commandGiveInvalidType, args[4]));
                    return;
                }
            }
            ItemStack item = new ItemStack(type, amount);
            int customModelData = it.getCustomModelData();
            if (name != null || !lore.isEmpty() || customModelData > 0 || it.isEnchantment()) {
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                if (name != null) {
                    meta.setDisplayName(name);
                }
                if (!lore.isEmpty()) {
                    meta.setLore(lore);
                }
                if (customModelData > 0) {
                    meta.setCustomModelData(customModelData);
                }
                if (it.isEnchantment()) {
                    //noinspection DataFlowIssue
                    meta.addEnchant(Enchantment.getByName("DURABILITY"), 1, false);
                    if (plugin.mcVersion >= 8) { // 1.8才提供这个方法
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                }
                item.setItemMeta(meta);
            }
            addItem(player, item);
            sendMessage(sender, Language.replaceArgs(Language.commandGive, amount, name == null ? "§b" + type : name, player.getName()));
        }
    }

    private void addItem(Player player, ItemStack... item) {
        HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(item);
        if (!overflow.isEmpty()) {
            Location loc = player.getLocation();
            World world = player.getWorld();
            for (Map.Entry<Integer, ItemStack> entry : overflow.entrySet()) {
                ItemStack itemStack = entry.getValue();
                int amount = itemStack.getAmount();
                int max = itemStack.getMaxStackSize();
                while (amount > max) {
                    amount -= max;
                    itemStack.setAmount(max);
                    world.dropItemNaturally(loc, itemStack);
                }
                itemStack.setAmount(amount);
                world.dropItem(loc, itemStack);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void commandType(CommandSender sender) {
        if (sender instanceof Player) {
            ItemStack item = plugin.mcVersion > 8 ? ((Player) sender).getInventory().getItemInMainHand() : ((Player) sender).getItemInHand();
            if (item.getType() == Material.AIR) {
                sendMessage(sender, Language.commandTypeNotItem);
                return;
            }
            if (plugin.mcVersion >= 14 && item.getItemMeta() != null && item.getItemMeta().hasCustomModelData()) {
                sendMessage(sender, Language.replaceArgs(Language.commandType, item.getType() + " (CustomModel:" + item.getItemMeta().getCustomModelData() + ")"));
            } else {
                sendMessage(sender, Language.replaceArgs(Language.commandType, item.getType()));
            }
            return;
        }
        sendMessage(sender, Language.commandTypeConsole);
    }

}
