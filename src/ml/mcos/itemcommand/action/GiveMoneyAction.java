package ml.mcos.itemcommand.action;

import ml.mcos.itemcommand.config.Language;
import ml.mcos.itemcommand.util.Utils;
import org.bukkit.entity.Player;

public class GiveMoneyAction extends Action {

    public GiveMoneyAction(String value) {
        super(value);
    }

    @Override
    public void execute(Player player) {
        if (plugin.getEconomy() == null) {
            plugin.logMessage(Language.actionExecuteErrorGiveMoneyNotFoundEconomy);
            plugin.logMessage(Language.actionExecuteErrorGiveMoneyNotFoundEconomyTip);
        } else {
            double money = Utils.parseDouble(plugin.replacePlaceholders(player, value));
            if (money == -1.0) {
                plugin.logMessage(Language.replaceArgs(Language.actionExecuteErrorGiveMoneyInvalidValue, value));
            } else if (money > 0.0) {
                plugin.getEconomy().depositPlayer(player, money);
            }
        }
    }
}
