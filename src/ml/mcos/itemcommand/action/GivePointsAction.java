package ml.mcos.itemcommand.action;

import ml.mcos.itemcommand.config.Language;
import ml.mcos.itemcommand.util.Utils;
import org.bukkit.entity.Player;

public class GivePointsAction extends Action {

    public GivePointsAction(String value) {
        super(value);
    }

    @Override
    public void execute(Player player) {
        if (plugin.getPointsAPI() == null) {
            plugin.logMessage(Language.actionExecuteErrorGivePointsNotFoundPoints);
            plugin.logMessage(Language.actionExecuteErrorGivePointsNotFoundPointsTip);
        } else {
            int points = Utils.parseInt(plugin.replacePlaceholders(player, value));
            if (points == -1) {
                plugin.logMessage(Language.replaceArgs(Language.actionExecuteErrorGivePointsInvalidValue, value));
            } else if (points > 0) {
                plugin.getPointsAPI().give(player.getUniqueId(), points);
            }
        }
    }
}
