package ml.mcos.itemcommand;

import me.clip.placeholderapi.PlaceholderAPI;
import ml.mcos.itemcommand.config.Config;
import ml.mcos.itemcommand.item.Item;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
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
                } else {
                    sender.sendMessage("§a控制台无法使用此命令。");
                }
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
            if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
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
        if (time >= current) {
            player.sendMessage("§4使用冷却: §c" + (int) ((time - current) / 1000) + "§4秒。");
            return false;
        }
        return true;
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
