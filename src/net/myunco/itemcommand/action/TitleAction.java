package net.myunco.itemcommand.action;

import net.myunco.itemcommand.config.Language;
import net.myunco.itemcommand.util.Utils;
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
        if (plugin.mcVersion < 8 || (plugin.mcVersion == 8 && plugin.mcVersionPatch < 7)) {
            plugin.logMessage(Language.actionExecuteErrorTitleNotSupport);
            return;
        }
        String[] args = value.split(",");
        if (args.length != 3) {
            plugin.logMessage(Language.actionExecuteErrorTitleArgsError);
            return;
        }
        String title = plugin.replacePlaceholders(player, args[0]);
        String subtitle = plugin.replacePlaceholders(player, args[1]);
        int time = Utils.parseInt(plugin.replacePlaceholders(player, args[2]));
        if (all) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (!sendTitle(p, title, subtitle, time)) {
                    return;
                }
            }
        } else {
            sendTitle(player, title, subtitle, time);
        }
    }

    @SuppressWarnings("deprecation")
    private boolean sendTitle(Player player, String title, String subtitle, int time) {
        if (time == -1) {
            plugin.logMessage(Language.actionExecuteErrorTitleInvalidTime);
            return false;
        }
        if (time > 0) {
            if (plugin.mcVersion > 10) {
                player.sendTitle(title, subtitle, 10, time * 20, 10);
            } else {
                player.sendTitle(title, subtitle);
            }
        }
        return true;
    }

}
