package ml.mcos.itemcommand;

import me.clip.placeholderapi.PlaceholderAPI;
import ml.mcos.itemcommand.config.Config;
import ml.mcos.itemcommand.item.Item;
import ml.mcos.itemcommand.util.Utils;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

//1.0.0版本需求：实现配置文件中演示的所有功能
public class ItemCommand extends JavaPlugin implements Listener {
    private static ItemCommand plugin;
    private Economy economy;
    private PlayerPointsAPI pointsAPI;
    private boolean enablePAPI;
    private final HashMap<String, Long> cdMap = new HashMap<>();

    @Override
    public void onEnable() {
        plugin = this;
        setupEconomy();
        setupPointsAPI();
        enablePAPI = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
        initConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void initConfig() {
        Config.loadConfig(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public void setupEconomy() {
        if (!getServer().getPluginManager().isPluginEnabled("Vault")) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        economy = rsp.getProvider();
        getLogger().info("Using economy system: §3" + economy.getName());
    }

    public void setupPointsAPI() {
        PlayerPoints playerPoints = (PlayerPoints) getServer().getPluginManager().getPlugin("PlayerPoints");
        if (playerPoints == null || !playerPoints.isEnabled()) {
            return;
        }
        pointsAPI = playerPoints.getAPI();
        getLogger().info("Found PlayerPoints: §3v" + playerPoints.getDescription().getVersion());
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
                commandList(sender);
                break;
            case "reload":
                initConfig();
                sender.sendMessage("§a配置文件重载完成");
                break;
            case "version":
                sender.sendMessage("§a当前版本: §b" + getDescription().getVersion());
                break;
            default:
                sender.sendMessage("§6未知的子命令");
        }
        return true;
    }

    private void commandAdd(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
            boolean def = args.length == 1;
            boolean flag = false;
            ItemMeta meta = item.getItemMeta();
            String id = String.valueOf(System.currentTimeMillis());
            if (def || containsIgnoreCase(args, "name")) {
                if (meta == null || !meta.hasDisplayName()) {
                    sender.sendMessage("§a你手中的物品没有显示名称，无法添加name项。");
                } else {
                    Config.config.set(id + ".name", meta.getDisplayName());
                    flag = true;
                }
            }
            if (!def && containsIgnoreCase(args, "lore")) {
                if (meta == null || !meta.hasLore()) {
                    sender.sendMessage("§a你手中的物品没有Lore，无法添加lore项。");
                } else {
                    Config.config.set(id + ".lore", meta.getLore());
                    flag = true;
                }
            }
            if (!def && containsIgnoreCase(args, "type")) {
                Config.config.set(id + ".type", item.getType().toString());
                flag = true;

            }
            if (flag) {
                Config.saveConfig();
                sender.sendMessage("§a已添加到配置文件, ID为: " + id + ", 快去修改吧!");
            }
            return;
        }
        sender.sendMessage("§a控制台无法使用此命令。");
    }

    private void commandGive(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("用法: /ic give <玩家> <物品ID> <物品数量> [物品类型]");
            sender.sendMessage("(物品类型参数仅在指定ID的物品配置中未指定物品类型时生效, 默认为石头)");
            sender.sendMessage("<> = 必选参数 [] = 可选参数");
            return;
        }
        Player player = getServer().getPlayer(args[1]);
        if (player == null) {
            sender.sendMessage("§c指定的玩家不在线或不存在!");
        } else if (!Config.idList.contains(args[2])) {
            sender.sendMessage("§c指定的ID不存在或未能正确加载.");
        } else {
            int amount = Utils.parseInt(args[3]);
            if (amount < 1) {
                sender.sendMessage("§c错误: 物品数量不能小于1!");
                return;
            }
            String name = Config.config.getString(args[2] + ".name");
            List<String> lore = Config.config.getStringList(args[2] + ".lore");
            String typeString = Config.config.getString(args[2] + ".type");
            if (typeString == null) {
                typeString = args.length == 4 ? "STONE" : args[4];
            }
            Material type;
            try {
                type = Material.valueOf(typeString);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§c无效的物品类型: " + typeString);
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
            sender.sendMessage("§a已将§b" + amount + "§a个" + (name == null ? typeString : name) + "§a添加到该玩家的物品栏.");
        }
    }

    private void commandList(CommandSender sender) {
        sender.sendMessage("§6已加载的物品ID列表: §a" + String.join(", ", Config.idList));
    }

    private boolean containsIgnoreCase(String[] array, String ele) {
        for (String s : array) {
            if (s.equalsIgnoreCase(ele)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND && event.hasItem()) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                ItemStack itemStack = event.getItem();
                Item item = Config.matchItem(itemStack);
                if (item != null) {
                    Player player = event.getPlayer();
                    //noinspection ConstantConditions
                    if (item.hasPermission(player) && item.meetRequiredAmount(player, itemStack) && !isCooling(player) && item.charge(player)) {
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

    private boolean isCooling(Player player) {
        Long time = cdMap.get(player.getName());
        if (time == null) {
            return false;
        }
        long current = System.currentTimeMillis();
        getLogger().info("time: " + time);
        getLogger().info("current: " + current);
        if (time >= current) {
            player.sendMessage("§4使用冷却: §c" + (int) ((time - current) / 1000) + "§4秒。");
            return true;
        }
        return false;
    }

    public String replacePlaceholders(Player player, String text) {
        if (enablePAPI && possibleContainPlaceholders(text)) {
            return PlaceholderAPI.setPlaceholders(player, text.indexOf('{') == -1 ? text : text.replace("{player}", player.getName()));
        } else {
            return text.indexOf('{') == -1 ? text : text.replace("{player}", player.getName());
        }
    }

    private static boolean possibleContainPlaceholders(String text) {
        char[] value = text.toCharArray();
        int count = 0;
        for (char c : value) {
            if (c == '%') {
                count++;
                if (count == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ItemCommand getPlugin() {
        return plugin;
    }

    public Economy getEconomy() {
        return economy;
    }

    public PlayerPointsAPI getPointsAPI() {
        return pointsAPI;
    }
}
