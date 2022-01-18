package ml.mcos.itemcommand.action;

import ml.mcos.itemcommand.ItemCommand;
import org.bukkit.entity.Player;

public class Action {
    public static ItemCommand plugin = ItemCommand.getPlugin();
    protected final String value;

    public Action(String value) {
        this.value = value;
    }

    public void execute(Player player) {}
}
