package ml.mcos.itemcommand;

import me.clip.placeholderapi.PlaceholderAPI;
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
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
//代码暂未实现配置文件列出的功能
public class ItemCommand extends JavaPlugin implements Listener {
    private Set<String> names;
    private boolean enablePAPI;

    @Override
    public void onEnable() {
        enablePAPI = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
        getServer().getPluginManager().registerEvents(this, this);
        initConfig();
    }

    private void initConfig() {
        saveDefaultConfig();
        reloadConfig();
        names = getConfig().getKeys(false);
        getLogger().info("Loaded " + names.size() + "items.");
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return false;
        }
        switch (args[0].toLowerCase()) {
            case "add":
                if (sender instanceof Player) {
                    ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null || !meta.hasDisplayName()) {
                        sender.sendMessage("§a你手中的物品没有显示名称，无法添加。");
                    } else {
                        getConfig().set(meta.getDisplayName() + ".player-command", Arrays.asList("cmd1", "cmd2"));
                        saveConfig();
                        sender.sendMessage("§a已添加到配置文件，去修改吧！");
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

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND && event.hasItem()) {
            if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                ItemStack item = event.getItem();
                assert item != null;
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                String name = meta.getDisplayName();
                if (names.contains(name)) {
                    Player player = event.getPlayer();
                    List<String> playerCommand = getConfig().getStringList(name + ".player-command");
                    List<String> playerOpCommand = getConfig().getStringList(name + ".player-op-command");
                    List<String> consoleCommand = getConfig().getStringList(name + ".console-command");
                    List<String> playerMessage = getConfig().getStringList(name + ".player-message");
                    for (String cmd : playerCommand) {
                        player.chat("/" + replacePlaceholders(player, cmd));
                    }
                    if (playerOpCommand.size() != 0) {
                        boolean flag = player.isOp();
                        player.setOp(true);
                        for (String cmd : playerOpCommand) {
                            player.chat("/" + replacePlaceholders(player, cmd));
                        }
                        player.setOp(flag);
                    }
                    for (String cmd : consoleCommand) {
                        getServer().dispatchCommand(getServer().getConsoleSender(), replacePlaceholders(player, cmd));
                    }
                    for (String msg : playerMessage) {
                        player.sendMessage(replacePlaceholders(player, msg));
                    }
                    item.setAmount(item.getAmount() - 1);
                    event.setCancelled(true);
                }
            }
        }
    }

    private String replacePlaceholders(Player player, String text) {
        if (enablePAPI && possibleContainPlaceholders(text)) {
            return PlaceholderAPI.setPlaceholders(player, text.replace("{player}", player.getName()));
        } else {
            return text.replace("{player}", player.getName());
        }
    }

    private boolean possibleContainPlaceholders(String text) {
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

}
