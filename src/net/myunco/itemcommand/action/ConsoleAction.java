package net.myunco.itemcommand.action;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ConsoleAction extends Action {
    private static final ConsoleCommandSender consoleSender = plugin.getServer().getConsoleSender();

    public ConsoleAction(String value) {
        super(value);
    }

    @Override
    public void execute(Player player) {
        String command = plugin.replacePlaceholders(player, value);
        // 兼容Folia 使用GlobalRegionScheduler运行控制台命令
        plugin.getScheduler().runTask(() -> plugin.getServer().dispatchCommand(consoleSender, command));
        plugin.logMessage("console dispatch command: " + command);
    }
}
