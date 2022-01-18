package ml.mcos.itemcommand.action;

import org.bukkit.entity.Player;

public class ChatAction extends Action {

    public ChatAction(String value) {
        super(value);
    }

    @Override
    public void execute(Player player) {
        player.chat(plugin.replacePlaceholders(player, value));
    }
}
