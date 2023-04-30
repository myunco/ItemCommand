package ml.mcos.itemcommand.item.expression;

import ml.mcos.itemcommand.config.Language;
import ml.mcos.itemcommand.util.Utils;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class SimpleDecimalExpression implements Expression {
    private final String left;
    private final String right;
    private final String operator;
    private final String message;

    public SimpleDecimalExpression(String expression) {
        operator = getOperator(expression);
        if (operator == null) {
            plugin.logMessage(Language.replaceArgs(Language.loadItemErrorNotFoundOperator, expression));
        }
        left = Utils.getTextLeft(expression, operator).trim();
        right = Utils.getTextRight(Utils.getTextLeft1(expression, ','), operator).trim();
        message = Utils.getTextRight1(expression, ',').trim();
    }

    @Override
    public boolean execute(Player player) {
        if (operator == null) {
            return false;
        }
        BigDecimal left;
        BigDecimal right;
        try {
            left = new BigDecimal(plugin.replacePlaceholders(player, this.left));
        } catch (NumberFormatException e) {
            plugin.logMessage(Language.replaceArgs(Language.useItemConditionInvalidNumber, plugin.replacePlaceholders(player, this.left)));
            return false;
        }
        try {
            right = new BigDecimal(plugin.replacePlaceholders(player, this.right));
        } catch (NumberFormatException e) {
            plugin.logMessage(Language.replaceArgs(Language.useItemConditionInvalidNumber, plugin.replacePlaceholders(player, this.right)));
            return false;
        }
        switch (operator) {
            case "=":
            case "==":
                return left.compareTo(right) == 0;
            case "!=":
            case "!==":
                return left.compareTo(right) != 0;
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
        return message;
    }

}
