package ml.mcos.itemcommand.update;

import ml.mcos.itemcommand.ItemCommand;
import ml.mcos.itemcommand.config.Language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateChecker {
    private static final ItemCommand plugin = ItemCommand.getPlugin();
    private static Timer timer;
    private static String downloadLink;

    public static void start() {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        CheckResult result = checkVersionUpdate("https://myunco.sinacloud.net/B821CED3/version.txt");
                        if (result.getResultType() == CheckResult.ResultType.SUCCESS) {
                            if (result.hasNewVersion()) {
                                String str = Language.replaceArgs(Language.updateFoundNewVersion, CheckResult.currentVersion, result.getLatestVersion());
                                plugin.logMessage(result.hasMajorUpdate() ? Language.updateMajorUpdate + str : str);
                                // plugin.logMessage(Language.updateDownloadLink + "https://www.mcbbs.net/thread-1297777-1-1.html");
                                plugin.logMessage(Language.updateDownloadLink + downloadLink);
                            }
                        } else {
                            plugin.logMessage(Language.updateCheckFailure + result.getResponseCode());
                        }
                    } catch (IOException e) {
                        plugin.logMessage(Language.updateCheckException);
                        e.printStackTrace();
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

    public static CheckResult checkVersionUpdate(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        int code = conn.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String latestVersion = reader.readLine();
            downloadLink = reader.readLine();
            reader.close();
            conn.disconnect();
            return new CheckResult(latestVersion, code, CheckResult.ResultType.SUCCESS);
        } else {
            return new CheckResult(code, CheckResult.ResultType.FAILURE);
        }
    }

}
