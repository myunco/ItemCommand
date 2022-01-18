package ml.mcos.itemcommand.action;

import org.bukkit.entity.Player;

public class ConsoleAction extends Action {

    public ConsoleAction(String value) {
        super(value);
    }

    @Override
    public void execute(Player player) {
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), plugin.replacePlaceholders(player, value));
    }
}
