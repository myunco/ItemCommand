package ml.mcos.itemcommand.action;

import ml.mcos.itemcommand.config.Language;
import ml.mcos.itemcommand.util.Utils;
import org.bukkit.entity.Player;

public class TitleAction extends Action {
    private final boolean all;

    public TitleAction(String value) {
        this(value, false);
    }

    public TitleAction(String value, boolean all) {
        super(value);
        this.all = all;
    }

    @Override
    public void execute(Player player) {
        if (plugin.getMcVersion() <= 10) {
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
                int time = Utils.parseInt(plugin.replacePlaceholders(p, args[2]));
                if (time == -1) {
                    plugin.logMessage(Language.actionExecuteErrorTitleInvalidTime);
                    return;
                } else if (time > 0) {
                    p.sendTitle(plugin.replacePlaceholders(p, args[0]), plugin.replacePlaceholders(p, args[1]), 10, time * 20, 10);
                }
            }
        } else {
            int time = Utils.parseInt(plugin.replacePlaceholders(player, args[2]));
            if (time == -1) {
                plugin.logMessage(Language.actionExecuteErrorTitleInvalidTime);
            } else if (time > 0) {
                player.sendTitle(plugin.replacePlaceholders(player, args[0]), plugin.replacePlaceholders(player, args[1]), 10, time * 20, 10);
            }
        }
    }
}
