package ml.mcos.itemcommand.action;

import ml.mcos.itemcommand.util.Utils;
import org.bukkit.entity.Player;

public class GiveMoneyAction extends Action {

    public GiveMoneyAction(String value) {
        super(value);
    }

    @Override
    public void execute(Player player) {
        if (plugin.getEconomy() == null) {
            plugin.getLogger().warning("未找到经济插件, 无法执行 give-money 动作!");
            plugin.getLogger().warning("请检查是否正确安装Vault插件以及经济提供插件! (如Essentials、CMI、Economy等)");
        } else {
            double money = Utils.parseDouble(plugin.replacePlaceholders(player, value));
            if (money == -1.0) {
                plugin.getLogger().warning("错误: 无法执行 give-money 动作! 原因: 无效的数字格式: " + value);
            } else if (money > 0.0) {
                plugin.getEconomy().depositPlayer(player, money);
            }
        }
    }
}
