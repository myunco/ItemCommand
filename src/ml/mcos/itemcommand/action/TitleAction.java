package ml.mcos.itemcommand.action;

import ml.mcos.itemcommand.config.Language;
import ml.mcos.itemcommand.util.Utils;
import org.bukkit.entity.Player;

public class TitleAction extends Action {
    private final boolean all;
    private final int mcVersion = plugin.getMcVersion();

    public TitleAction(String value) {
        this(value, false);
    }

    public TitleAction(String value, boolean all) {
        super(value);
        this.all = all;
    }

    @Override
    public void execute(Player player) {
        if (mcVersion < 8 || (mcVersion == 8 && plugin.getMcVersionPatch() < 7)) {
            plugin.logMessage(Language.actionExecuteErrorTitleNotSupport);
            return;
        }
        String[] args = value.split(",");
        if (args.length != 3) {
            plugin.logMessage(Language.actionExecuteErrorTitleArgsError);
            return;
        }
        if (all) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (!sendTitle(p, plugin.replacePlaceholders(p, args[0]), plugin.replacePlaceholders(p, args[1]), Utils.parseInt(plugin.replacePlaceholders(p, args[2])))) {
                    return;
                }
            }
        } else {
            sendTitle(player, plugin.replacePlaceholders(player, args[0]), plugin.replacePlaceholders(player, args[1]), Utils.parseInt(plugin.replacePlaceholders(player, args[2])));
        }
    }

    @SuppressWarnings("deprecation")
    private boolean sendTitle(Player player, String title, String subtitle, int time) {
        if (time == -1) {
            plugin.logMessage(Language.actionExecuteErrorTitleInvalidTime);
            return false;
        }
        if (time > 0) {
            if (mcVersion > 10) {
                player.sendTitle(title, subtitle, 10, time * 20, 10);
            } else {
                player.sendTitle(title, subtitle);
            }
        }
        return true;
    }

}
