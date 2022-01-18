package ml.mcos.itemcommand.action;

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
            player.chat("/" + plugin.replacePlaceholders(player, value));
        } finally {
            player.setOp(op);
        }
    }
}
