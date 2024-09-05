package ml.mcos.itemcommand.update;

import ml.mcos.itemcommand.ItemCommand;
import ml.mcos.itemcommand.config.Language;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateChecker {
    private static final ItemCommand plugin = ItemCommand.getPlugin();
    private static Timer timer;

    public static void start() {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    CheckResult result = new CheckResult("https://myunco.sinacloud.net/B821CED3/version.txt", plugin.getDescription().getVersion());
                    if (result.getResultType() == CheckResult.ResultType.SUCCESS) {
                        if (result.hasNewVersion()) {
                            String str = Language.replaceArgs(Language.updateFoundNewVersion, result.getCurrentVersion(), result.getLatestVersion());
                            plugin.logMessage(result.hasMajorUpdate() ? Language.updateMajorUpdate + str : str);
                            plugin.logMessage(Language.updateDownloadLink + result.getDownloadLink());
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
