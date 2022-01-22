package ml.mcos.itemcommand;

import me.clip.placeholderapi.PlaceholderAPI;
import ml.mcos.itemcommand.command.ICCommand;
import ml.mcos.itemcommand.config.Config;
import ml.mcos.itemcommand.config.ItemInfo;
import ml.mcos.itemcommand.config.Language;
import ml.mcos.itemcommand.listener.PlayerInteractEventListener;
import ml.mcos.itemcommand.metrics.Metrics;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemCommand extends JavaPlugin {
    private static ItemCommand plugin;
    private Economy economy;
    private PlayerPoints points;
    private boolean enablePAPI;

    @Override
    public void onEnable() {
        plugin = this;
        init();
        getServer().getPluginManager().registerEvents(new PlayerInteractEventListener(), this);
        PluginCommand command = getCommand("ItemCommand");
        if (command != null) {
            command.setExecutor(new ICCommand(this));
        }
        Metrics metrics = new Metrics(this, 14020);
        metrics.addCustomChart(new Metrics.SimplePie("economy_plugin", () -> economy == null ? "Not found" : economy.getName()));
        metrics.addCustomChart(new Metrics.SimplePie("playerpoints_version", () -> points == null ? "Not found" : points.getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public void init() {
        if (economy == null) {
            setupEconomy();
        }
        if (points == null) {
            setupPoints();
        }
        enablePAPI = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
        Config.loadConfig(this);
        ItemInfo.loadItemInfo(this);
    }

    public static ItemCommand getPlugin() {
        return plugin;
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

    public void setupPoints() {
        PlayerPoints playerPoints = (PlayerPoints) getServer().getPluginManager().getPlugin("PlayerPoints");
        if (playerPoints == null || !playerPoints.isEnabled()) {
            return;
        }
        points = playerPoints;
        getLogger().info("Found PlayerPoints: §3v" + playerPoints.getDescription().getVersion());
    }

    public Economy getEconomy() {
        return economy;
    }

    public PlayerPointsAPI getPointsAPI() {
        return points.getAPI();
    }

    public String replacePlaceholders(Player player, String text) {
        if (enablePAPI && mayContainPlaceholders(text)) {
            return PlaceholderAPI.setPlaceholders(player, text.indexOf('{') == -1 ? text : text.replace("{player}", player.getName()));
        } else {
            return text.indexOf('{') == -1 ? text : text.replace("{player}", player.getName());
        }
    }

    private static boolean mayContainPlaceholders(String text) {
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

    public ClassLoader classLoader() {
        return getClassLoader();
    }

    public void logMessage(String message) {
        getServer().getConsoleSender().sendMessage(Language.logPrefix + message);
    }

}
