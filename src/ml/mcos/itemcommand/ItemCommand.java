package ml.mcos.itemcommand;

import me.clip.placeholderapi.PlaceholderAPI;
import ml.mcos.itemcommand.command.ICCommand;
import ml.mcos.itemcommand.config.Config;
import ml.mcos.itemcommand.config.ItemInfo;
import ml.mcos.itemcommand.config.Language;
import ml.mcos.itemcommand.listener.PlayerInvolveEventListener;
import ml.mcos.itemcommand.metrics.Metrics;
import ml.mcos.itemcommand.update.UpdateChecker;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemCommand extends JavaPlugin {
    private static ItemCommand plugin;
    private Economy economy;
    private PlayerPoints points;
    private boolean enablePAPI;
    private String papiVersion;
    private int mcVersion;
    private int mcVersionPatch;

    @Override
    public void onEnable() {
        plugin = this;
        mcVersion = getMinecraftVersion();
        getLogger().info("Minecraft version: 1" + mcVersion + mcVersionPatch);
        init();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getPluginManager().registerEvents(new PlayerInvolveEventListener(), this);
        PluginCommand command = getCommand("ItemCommand");
        if (command != null) {
            command.setExecutor(new ICCommand(this));
        }
        Metrics metrics = new Metrics(this, 14020);
        metrics.addCustomChart(new Metrics.SimplePie("economy_plugin", () -> economy == null ? "Not found" : economy.getName()));
        metrics.addCustomChart(new Metrics.SimplePie("playerpoints_version", () -> points == null ? "Not found" : points.getDescription().getVersion()));
        metrics.addCustomChart(new Metrics.SimplePie("placeholderapi_version", () -> papiVersion == null ? "Not found" : papiVersion));
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        UpdateChecker.stop();
    }

    public void init() {
        Config.loadConfig(this);
        if (Config.checkUpdate) {
            UpdateChecker.start();
        }
        if (economy == null) {
            setupEconomy();
        }
        if (points == null) {
            setupPoints();
        }
        Plugin papi = getServer().getPluginManager().getPlugin("PlaceholderAPI");
        enablePAPI = papi != null && papi.isEnabled();
        if (enablePAPI) {
            papiVersion = papi.getDescription().getVersion();
            logMessage("Found PlaceholderAPI: §3v" + papiVersion);
        }
        ItemInfo.loadItemInfo(this);
    }

    public static ItemCommand getPlugin() {
        return plugin;
    }

    private int getMinecraftVersion() {
        String[] version = getServer().getBukkitVersion().replace('-', '.').split("\\.");
        try {
            mcVersionPatch = Integer.parseInt(version[2]);
        } catch (NumberFormatException ignored) {
        }
        return Integer.parseInt(version[1]);
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
        logMessage("Using economy system: §3" + economy.getName());
    }

    public void setupPoints() {
        PlayerPoints playerPoints = (PlayerPoints) getServer().getPluginManager().getPlugin("PlayerPoints");
        if (playerPoints == null || !playerPoints.isEnabled()) {
            return;
        }
        points = playerPoints;
        logMessage("Found PlayerPoints: §3v" + playerPoints.getDescription().getVersion());
    }

    public Economy getEconomy() {
        return economy;
    }

    public PlayerPointsAPI getPointsAPI() {
        return points == null ? null : points.getAPI();
    }

    public String replacePlaceholders(Player player, String text) {
        if (text == null) {
            return null;
        }
        if (enablePAPI && mayContainPlaceholders(text)) {
            return PlaceholderAPI.setPlaceholders(player, text.indexOf('{') == -1 ? text : text.replace("{player}", player.getName()));
        }
        return text.indexOf('{') == -1 ? text : text.replace("{player}", player.getName());
    }

    public static boolean mayContainPlaceholders(String text) {
        char[] value = text.toCharArray();
        int count = 0;
        for (char c : value) {
            if (c == '%') {
                count++;
                if (count == 2) {
                    return text.indexOf('_') != -1;
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

    public int getMcVersion() {
        return mcVersion;
    }

}
