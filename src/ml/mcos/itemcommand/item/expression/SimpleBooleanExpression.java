package ml.mcos.itemcommand.item.expression;

import ml.mcos.itemcommand.action.Action;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class SimpleBooleanExpression implements Expression {
    private final String expr;
    private String operator;
    private final Action[] action;

    public SimpleBooleanExpression(String condition) {
        ArrayList<String> args = Expression.parseCondition(condition);
        if (args.get(0).charAt(0) == '!') {
            operator = "!";
            expr = args.get(0).substring(1);
        } else {
            expr = args.get(0);
        }
        action = Expression.parseAction(args);
    }

    @Override
    public boolean execute(Player player) {
        boolean result;
        switch (plugin.replacePlaceholders(player, expr).toLowerCase()) {
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
    public void executeAction(Player player) {
        for (Action act : action) {
            act.execute(player);
        }
    }

}
