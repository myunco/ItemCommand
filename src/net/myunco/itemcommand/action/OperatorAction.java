package net.myunco.itemcommand.action;

import net.myunco.itemcommand.config.Config;
import org.bukkit.entity.Player;

public class OperatorAction extends Action {

    public OperatorAction(String value) {
        super(value);
    }

    @Override
    public void execute(Player player) {
        boolean op = player.isOp();
        player.setOp(true);
        try {
            if (Config.usePerformCommand) {
                player.performCommand(plugin.replacePlaceholders(player, value));
            } else {
                //使用chat方法执行命令 使其他插件可以拦截或处理命令
                player.chat("/" + plugin.replacePlaceholders(player, value));
            }
        } finally {
            player.setOp(op);
        }
    }
}
