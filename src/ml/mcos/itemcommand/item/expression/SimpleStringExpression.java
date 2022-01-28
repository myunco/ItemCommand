package ml.mcos.itemcommand.item.expression;

import ml.mcos.itemcommand.config.Language;
import ml.mcos.itemcommand.util.Utils;
import org.bukkit.entity.Player;

public class SimpleStringExpression implements Expression {
    private final String left;
    private final String right;
    private final String operator;
    private final String message;

    public SimpleStringExpression(String expression) {
        operator = getOperator(expression);
        if (operator == null) {
            plugin.logMessage(Language.replaceArgs(Language.loadItemErrorNotFoundOperator, expression));
        }
        left = Utils.getTextLeft(expression, operator).trim();
        right = Utils.getTextRight(Utils.getTextLeft(expression, ','), operator).trim();
        message = Utils.getTextRight(expression, ',').trim();
    }

    @Override
    public boolean execute(Player player) {
        if (operator == null) {
            return false;
        }
        String left = plugin.replacePlaceholders(player, this.left);
        String right = plugin.replacePlaceholders(player, this.right);
        switch (operator) {
            case "=":
                return left.equals(right);
            case "!=":
                return !left.equals(right);
            case "==":
                return left.equalsIgnoreCase(right);
            case "!==":
                return !left.equalsIgnoreCase(right);
            case ">":
                return left.compareTo(right) > 0;
            case ">=":
                return left.compareTo(right) >= 0;
            case "<":
                return left.compareTo(right) < 0;
            case "<=":
                return left.compareTo(right) <= 0;
        }
        return false;
    }

    @Override
    public String getMessage(Player player) {
        return plugin.replacePlaceholders(player, message);
    }

}
