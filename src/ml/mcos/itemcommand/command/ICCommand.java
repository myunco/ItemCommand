package ml.mcos.itemcommand.command;

import ml.mcos.itemcommand.ItemCommand;
import ml.mcos.itemcommand.config.ItemInfo;
import ml.mcos.itemcommand.config.Language;
import ml.mcos.itemcommand.item.Item;
import ml.mcos.itemcommand.update.UpdateChecker;
import ml.mcos.itemcommand.util.Utils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        if (sender instanceof Player) {
            if (args.length > 1 && args[0].equalsIgnoreCase("add")) {
                ArrayList<String> list = new ArrayList<>(TabComplete.tabListMap.get("ItemCommand.add"));
                list.removeAll(mergeArgs(args));
                return list.isEmpty() ? list : TabComplete.getCompleteList(args, list);
            }
        }
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

    private static ArrayList<String> mergeArgs(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            if (args[i].isEmpty()) {
                continue;
            }
            list.add(args[i]);
        }
        return list;
    }

    private void commandAdd(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            //noinspection deprecation
            ItemStack item = plugin.mcVersion > 8 ? ((Player) sender).getInventory().getItemInMainHand() : ((Player) sender).getItemInHand();
            if (item.getType() == Material.AIR) {
                sendMessage(sender, Language.commandAddNotItem);
                return;
            }
            boolean def = args.length == 1;
            boolean flag = false;
            ItemMeta meta = item.getItemMeta();
            String id = String.valueOf(System.currentTimeMillis());
            if (def || containsIgnoreCase(args, "name")) {
                if (meta == null || !meta.hasDisplayName()) {
                    sendMessage(sender, Language.commandAddNotName);
                } else {
                    ItemInfo.config.set(id + ".name", meta.getDisplayName());
                    flag = true;
                }
            }
            if (!def) {
                if (containsIgnoreCase(args, "lore")) {
                    if (meta == null || !meta.hasLore()) {
                        sendMessage(sender, Language.commandAddNotLore);
                    } else {
                        ItemInfo.config.set(id + ".lore", meta.getLore());
                        flag = true;
                    }
                }
                if (containsIgnoreCase(args, "lore-exact")) {
                    ItemInfo.config.set(id + ".lore-exact", true);
                    flag = true;
                }
                if (containsIgnoreCase(args, "type")) {
                    ItemInfo.config.set(id + ".type", item.getType().toString());
                    flag = true;
                }
                if (containsIgnoreCase(args, "condition")) {
                    ItemInfo.config.set(id + ".condition", Collections.singletonList("true,"));
                }
                if (containsIgnoreCase(args, "trigger")) {
                    ItemInfo.config.set(id + ".trigger", Collections.singletonList("right"));
                }
                if (containsIgnoreCase(args, "action")) {
                    ItemInfo.config.set(id + ".action", Collections.singletonList("cmd: 示例命令"));
                }
                if (containsIgnoreCase(args, "price")) {
                    ItemInfo.config.set(id + ".price", 0);
                }
                if (containsIgnoreCase(args, "points")) {
                    ItemInfo.config.set(id + ".points", 0);
                }
                if (containsIgnoreCase(args, "levels")) {
                    ItemInfo.config.set(id + ".levels", 0);
                }
                if (containsIgnoreCase(args, "permission")) {
                    ItemInfo.config.set(id + ".permission", "示例权限");
                }
                if (containsIgnoreCase(args, "required-amount")) {
                    ItemInfo.config.set(id + ".required-amount", 1);
                }
                if (containsIgnoreCase(args, "cooldown")) {
                    ItemInfo.config.set(id + ".cooldown", 0);
                }
                if (plugin.mcVersion >= 14 && containsIgnoreCase(args, "customModelData") && meta != null) {
                    ItemInfo.config.set(id + ".customModelData", meta.getCustomModelData());
                }
            }
            if (flag) {
                ItemInfo.saveConfig();
                sendMessage(sender, Language.replaceArgs(Language.commandAdd, id));
            }
            return;
        }
        sendMessage(sender, Language.commandAddConsole);
    }

    private boolean containsIgnoreCase(String[] array, String ele) {
        for (String s : array) {
            if (s.equalsIgnoreCase(ele)) {
                return true;
            }
        }
        return false;
    }

    private void commandGive(CommandSender sender, String[] args) {
        if (args.length < 4) {
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
            int amount = Utils.parseInt(args[3]);
            if (amount == -1) {
                sendMessage(sender, Language.replaceArgs(Language.commandGiveInvalidAmount, amount));
                return;
            } else if (amount == 0) {
                sendMessage(sender, Language.commandGiveErrorAmount);
                return;
            }
            Item it = ItemInfo.getItemById(args[2]);
            assert it != null;
            String name = it.getName(player);
            List<String> lore = it.getLore(player);
            String typeString = ItemInfo.config.getString(args[2] + ".type");
            if (typeString == null) {
                typeString = args.length == 4 ? "STONE" : args[4];
            }
            Material type;
            try {
                type = Material.valueOf(typeString);
            } catch (IllegalArgumentException e) {
                sendMessage(sender, Language.replaceArgs(Language.commandGiveInvalidType, typeString));
                return;
            }
            ItemStack item = new ItemStack(type, amount);
            int customModelData = it.getCustomModelData();
            if (name != null || !lore.isEmpty() || customModelData > 0) {
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
                item.setItemMeta(meta);
            }
            player.getInventory().addItem(item);
            sendMessage(sender, Language.replaceArgs(Language.commandGive, amount, name == null ? typeString : name, player.getName()));
        }
    }

    private void commandType(CommandSender sender) {
        if (sender instanceof Player) {
            //noinspection deprecation
            ItemStack item = plugin.mcVersion > 8 ? ((Player) sender).getInventory().getItemInMainHand() : ((Player) sender).getItemInHand();
            if (item.getType() == Material.AIR) {
                sendMessage(sender, Language.commandTypeNotItem);
                return;
            }
            sendMessage(sender, Language.replaceArgs(Language.commandType, item.getType()));
            return;
        }
        sendMessage(sender, Language.commandTypeConsole);
    }

}
