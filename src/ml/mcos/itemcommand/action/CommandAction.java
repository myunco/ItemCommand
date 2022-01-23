package ml.mcos.itemcommand.action;

import org.bukkit.entity.Player;

public class CommandAction extends Action{

    public CommandAction(String value) {
        super(value);
    }

    @Override
    public void execute(Player player) {
        //使用chat方法执行命令 使其他插件可以拦截或处理命令
        player.chat("/" + plugin.replacePlaceholders(player, value));
    }
}
