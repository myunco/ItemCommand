package ml.mcos.itemcommand.action;

import org.bukkit.entity.Player;

public class BroadcastAction extends Action {

    public BroadcastAction(String value) {
        super(value);
    }

    @Override
    public void execute(Player player) {
        plugin.getServer().broadcastMessage(plugin.replacePlaceholders(player, value));
    }
}
