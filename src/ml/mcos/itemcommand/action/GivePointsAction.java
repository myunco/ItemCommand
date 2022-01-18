package ml.mcos.itemcommand.action;

import ml.mcos.itemcommand.util.Utils;
import org.bukkit.entity.Player;

public class GivePointsAction extends Action {

    public GivePointsAction(String value) {
        super(value);
    }

    @Override
    public void execute(Player player) {
        if (plugin.getEconomy() == null) {
            plugin.getLogger().warning("未找到点券插件, 无法执行 give-points 动作!");
            plugin.getLogger().warning("请检查是否正确安装PlayerPoints插件!");
        } else {
            int points = Utils.parseInt(plugin.replacePlaceholders(player, value));
            if (points == -1) {
                plugin.getLogger().warning("错误: 无法执行 give-points 动作! 原因: 无效的数字格式: " + value);
            } else if (points > 0) {
                plugin.getPointsAPI().give(player.getUniqueId(), points);
            }
        }
    }
}
