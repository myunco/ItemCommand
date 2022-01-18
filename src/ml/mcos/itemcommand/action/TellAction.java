package ml.mcos.itemcommand.action;

import org.bukkit.entity.Player;

public class TellAction extends Action {

    public TellAction(String value) {
        super(value);
    }

    @Override
    public void execute(Player player) {
        player.sendMessage(plugin.replacePlaceholders(player, value));
    }
}
