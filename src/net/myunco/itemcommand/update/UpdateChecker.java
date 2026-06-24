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
    static String url = "https://r2.699101.xyz/B821CED3/ItemCommand.txt";

    public static void start() {
        plugin.getScheduler().runTask(() -> {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    CheckResult result = new CheckResult(url, plugin.getDescription().getVersion());
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
                        url = "https://pub-860afec4fd3b4eaa9ed6e814d32c1379.r2.dev/B821CED3/ItemCommand.txt"; // 切换为备用链接
                    }
                }
            }, 6000, 12 * 60 * 60 * 1000);
        });
    }

    public static void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

}
