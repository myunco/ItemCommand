package net.myunco.itemcommand;

import me.clip.placeholderapi.PlaceholderAPI;
import net.myunco.folia.FoliaCompatibleAPI;
import net.myunco.folia.task.CompatibleScheduler;
import net.myunco.itemcommand.command.ICCommand;
import net.myunco.itemcommand.config.Config;
import net.myunco.itemcommand.config.CooldownInfo;
import net.myunco.itemcommand.config.ItemInfo;
import net.myunco.itemcommand.config.Language;
import net.myunco.itemcommand.listener.PlayerInvolveEventListener;
import net.myunco.itemcommand.metrics.Metrics;
import net.myunco.itemcommand.update.UpdateChecker;
import net.myunco.itemcommand.update.UpdateNotification;
import net.milkbowl.vault.economy.Economy;
import net.myunco.itemcommand.util.CompatibleEconomy;
import net.myunco.itemcommand.util.Version;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ItemCommand extends JavaPlugin {
    private static ItemCommand plugin;
    private CompatibleEconomy economy;
    private PlayerPoints points;
    private boolean enablePAPI;
    private String papiVersion;
    public Version mcVersion;
    private CompatibleScheduler scheduler;

    @Override
    public void onEnable() {
        plugin = this;
        mcVersion = new Version(getServer().getBukkitVersion());
        getLogger().info("Minecraft version: " + mcVersion);
        initScheduler();
        if (scheduler == null) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        init();
        initCommand();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getPluginManager().registerEvents(new PlayerInvolveEventListener(), this);
        if (Config.checkUpdate) {
            getServer().getPluginManager().registerEvents(new UpdateNotification(), this);
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
        if (!enablePAPI) {
            Plugin papi = getServer().getPluginManager().getPlugin("PlaceholderAPI");
            enablePAPI = papi != null && papi.isEnabled();
            if (enablePAPI) {
                papiVersion = papi.getDescription().getVersion();
                logMessage("Found PlaceholderAPI: §3v" + papiVersion);
            }
        }
        ItemInfo.loadItemInfo(this);
        CooldownInfo.loadCooldownInfo(PlayerInvolveEventListener.cdMap);
    }

    private void initCommand() {
        PluginCommand command = getCommand("ItemCommand");
        if (command != null) {
            command.setExecutor(new ICCommand(this));
            command.setTabCompleter((TabCompleter) command.getExecutor());
        }
    }

    public static ItemCommand getPlugin() {
        return plugin;
    }

    private void setupEconomy() {
        if (!getServer().getPluginManager().isPluginEnabled("Vault")) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        boolean old = false;
        try {
            Economy.class.getMethod("hasAccount", OfflinePlayer.class);
        } catch (NoSuchMethodException e) {
            old = true;
        }
        economy = new CompatibleEconomy(rsp.getProvider(), old);
        logMessage("Using economy system: §3" + economy.getName() + " v" + rsp.getPlugin().getDescription().getVersion());
    }

    private void setupPoints() {
        PlayerPoints playerPoints = (PlayerPoints) getServer().getPluginManager().getPlugin("PlayerPoints");
        if (playerPoints == null || !playerPoints.isEnabled()) {
            return;
        }
        points = playerPoints;
        logMessage("Found PlayerPoints: §3v" + playerPoints.getDescription().getVersion());
    }

    private void initScheduler() {
        Plugin api = getServer().getPluginManager().getPlugin("FoliaCompatibleAPI");
        if (api == null) {
            getLogger().warning("FoliaCompatibleAPI not found!");
            File file = new File(getDataFolder().getParentFile(), "FoliaCompatibleAPI-1.1.0.jar");
            InputStream in = getResource("lib/FoliaCompatibleAPI-1.1.0.jar");
            try {
                saveResource(file, in);
                api = getServer().getPluginManager().loadPlugin(file);
                if (api == null) {
                    throw new Exception("FoliaCompatibleAPI load failed!");
                }
                getServer().getPluginManager().enablePlugin(api);
                api.onLoad();
            } catch (Exception e) {
                e.printStackTrace();
                getLogger().severe("未安装 FoliaCompatibleAPI ，本插件无法运行！");
                return;
            }
        }
        scheduler = ((FoliaCompatibleAPI) api).getScheduler(this);
        getServer().getConsoleSender().sendMessage("[ItemCommand] Found FoliaCompatibleAPI: §3v" + api.getDescription().getVersion());
    }

    private void saveResource(File target, InputStream source) throws Exception {
        if (source != null) {
            //noinspection IOStreamConstructor
            OutputStream out = new FileOutputStream(target);
            byte[] buf = new byte[8192];
            int len;
            while ((len = source.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            source.close();
        }
    }

    public CompatibleEconomy getEconomy() {
        return economy;
    }

    public PlayerPointsAPI getPointsAPI() {
        return points == null ? null : points.getAPI();
    }

    public CompatibleScheduler getScheduler() {
        return scheduler;
    }

    public String replacePlaceholders(Player player, String text) {
        if (text == null) {
            return null;
        } else if (text.length() < 3) {
            return text;
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

}
