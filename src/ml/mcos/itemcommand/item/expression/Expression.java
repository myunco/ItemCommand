package ml.mcos.itemcommand.item.expression;

import ml.mcos.itemcommand.ItemCommand;
import org.bukkit.entity.Player;

public interface Expression {
    ItemCommand plugin = ItemCommand.getPlugin();

    boolean execute(Player player);

    String getMessage(Player player);

    default String getOperator(String expression) {
        String[] values = expression.split(" ");
        if (values.length < 3) {
            return null;
        }
        for (int i = 1; i < values.length - 1; i++) {
            switch (values[i]) {
                case "=":
                case "!=":
                case "==":
                case "!==":
                case ">":
                case ">=":
                case "<":
                case "<=":
                    return values[i];
            }
        }
        return null;
    }

}
