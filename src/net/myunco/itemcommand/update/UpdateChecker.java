package net.myunco.itemcommand.update;

import net.myunco.itemcommand.ItemCommand;
import net.myunco.itemcommand.config.Language;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateChecker {
    private static final ItemCommand plugin = ItemCommand.getPlugin();
    private static Timer timer;
    static boolean isUpdateAvailable;
    static String newVersion;
    static String downloadLink;

    public static void start() {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    CheckResult result = new CheckResult("https://myunco.sinacloud.net/B821CED3/ItemCommand.txt", plugin.getDescription().getVersion());
                    if (result.getResultType() == CheckResult.ResultType.SUCCESS) {
                        if (result.hasNewVersion()) {
                            isUpdateAvailable = true;
                            String str = Language.replaceArgs(Language.updateFoundNewVersion, result.getCurrentVersion(), result.getLatestVersion());
                            newVersion = result.hasMajorUpdate() ? Language.updateMajorUpdate + str : str;
                            downloadLink = Language.updateDownloadLink + result.getDownloadLink();
                            plugin.logMessage(newVersion);
                            plugin.logMessage(downloadLink);
                            plugin.logMessage(result.getUpdateInfo());
                        } else {
                            isUpdateAvailable = false;
                        }
                    } else {
                        plugin.logMessage(Language.updateCheckFailure + result.getErrorMessage());
                    }
                }
            }, 7000, 12 * 60 * 60 * 1000);
        });
    }

    public static void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

}
