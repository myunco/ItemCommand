package ml.mcos.itemcommand.command;

import ml.mcos.itemcommand.ItemCommand;
import ml.mcos.itemcommand.TabComplete;
import ml.mcos.itemcommand.config.ItemInfo;
import ml.mcos.itemcommand.config.Language;
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
                sendMessage(sender, "§6已加载的物品ID列表: §a" + String.join(", ", ItemInfo.idList));
                break;
            case "reload":
                UpdateChecker.stop();
                plugin.init();
                sendMessage(sender, "§a配置文件重载完成");
                break;
            case "version":
                sendMessage(sender, "§a当前版本: §b" + plugin.getDescription().getVersion());
                break;
            default:
                sendMessage(sender, "§6未知的子命令");
        }
        return true;
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
            ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) {
                sendMessage(sender, "§d你确定你手里有物品？");
                return;
            }
            boolean def = args.length == 1;
            boolean flag = false;
            ItemMeta meta = item.getItemMeta();
            String id = String.valueOf(System.currentTimeMillis());
            if (def || containsIgnoreCase(args, "name")) {
                if (meta == null || !meta.hasDisplayName()) {
                    sendMessage(sender, "§a你手中的物品没有显示名称, 无法添加name项。");
                } else {
                    ItemInfo.config.set(id + ".name", meta.getDisplayName());
                    flag = true;
                }
            }
            if (!def && containsIgnoreCase(args, "lore")) {
                if (meta == null || !meta.hasLore()) {
                    sendMessage(sender, "§a你手中的物品没有Lore, 无法添加lore项。");
                } else {
                    ItemInfo.config.set(id + ".lore", meta.getLore());
                    flag = true;
                }
            }
            if (!def && containsIgnoreCase(args, "type")) {
                ItemInfo.config.set(id + ".type", item.getType().toString());
                flag = true;
            }
            if (flag) {
                ItemInfo.saveConfig();
                sendMessage(sender, "§a已添加到配置文件, ID为: " + id + ", 快去修改吧!");
            }
            return;
        }
        sendMessage(sender, "§a控制台无法使用此命令。");
    }

    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(Language.messagePrefix + message);
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
            sendMessage(sender, "§6用法: /ic give <玩家> <物品ID> <物品数量> [物品类型]");
            sendMessage(sender, "§7(物品类型参数仅在指定ID的物品配置中未指定物品类型时生效, 默认为石头)");
            sendMessage(sender, "§b<> = 必填参数 [] = 可选参数");
            return;
        }
        Player player = plugin.getServer().getPlayer(args[1]);
        if (player == null) {
            sendMessage(sender, "§c指定的玩家不在线或不存在!");
        } else if (!ItemInfo.idList.contains(args[2])) {
            sendMessage(sender, "§c指定的ID不存在或未能正确加载.");
        } else {
            int amount = Utils.parseInt(args[3]);
            if (amount < 1) {
                sendMessage(sender, "§c错误: 物品数量不能小于1!");
                return;
            }
            String name = ItemInfo.config.getString(args[2] + ".name");
            List<String> lore = ItemInfo.config.getStringList(args[2] + ".lore");
            String typeString = ItemInfo.config.getString(args[2] + ".type");
            if (typeString == null) {
                typeString = args.length == 4 ? "STONE" : args[4];
            }
            Material type;
            try {
                type = Material.valueOf(typeString);
            } catch (IllegalArgumentException e) {
                sendMessage(sender, "§c无效的物品类型: " + typeString);
                return;
            }
            ItemStack item = new ItemStack(type, amount);
            if (name != null || !lore.isEmpty()) {
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                if (name != null) {
                    meta.setDisplayName(name);
                }
                if (!lore.isEmpty()) {
                    meta.setLore(lore);
                }
                item.setItemMeta(meta);
            }
            player.getInventory().addItem(item);
            sendMessage(sender, "§a已将§b" + amount + "§a个" + (name == null ? typeString : name) + "§a添加到该玩家的物品栏.");
        }
    }

}
