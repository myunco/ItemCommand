package ml.mcos.itemcommand.item.expression;

import ml.mcos.itemcommand.util.Utils;
import org.bukkit.entity.Player;

public class SimpleBooleanExpression implements Expression {
    private final String value;
    private String operator;
    private final String message;

    public SimpleBooleanExpression(String expression) {
        if (expression.charAt(0) == '!') {
            operator = "!";
            expression = expression.substring(1);
        }
        /*
        if (expression.indexOf(',') == -1) {
            value = expression;
            message = null;
        } else {
            value = Utils.getTextLeft(expression, ',').trim();
            message = Utils.getTextRight(expression, ',').trim();
        }
        */
        value = Utils.getTextLeft1(expression, ',').trim();
        message = Utils.getTextRight1(expression, ',').trim();
    }

    @Override
    public boolean execute(Player player) {
        boolean result;
        switch (plugin.replacePlaceholders(player, value).toLowerCase()) {
            case "true":
            case "yes":
                result = true;
                break;
            default:
                result = false;
        }
        return (operator == null) == result;
    }

    @Override
    public String getMessage(Player player) {
        return message;
    }

}
