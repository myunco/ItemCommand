package ml.mcos.itemcommand.action;

import org.bukkit.entity.Player;

public class CommandAction extends Action{

    public CommandAction(String value) {
        super(value);
    }

    @Override
    public void execute(Player player) {
        player.chat("/" + plugin.replacePlaceholders(player, value));
    }
}
