package net.myunco.itemcommand.item.expression;

import net.myunco.itemcommand.action.Action;
import net.myunco.itemcommand.config.Language;
import net.myunco.itemcommand.util.Utils;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class SimpleStringExpression implements Expression {
    private final String left;
    private final String right;
    private final String operator;
    private final Action[] action;

    public SimpleStringExpression(String condition) {
        ArrayList<String> args = Expression.parseCondition(condition);
        operator = Expression.getOperator(args.get(0));
        if (operator == null) {
            plugin.logMessage(Language.replaceArgs(Language.loadItemErrorNotFoundOperator, args.get(0)));
        }
        left = Utils.getTextLeft(args.get(0), operator).trim();
        right = Utils.getTextRight(args.get(0), operator).trim();
        action = Expression.parseAction(args);
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
    public void executeAction(Player player) {
        for (Action act : action) {
            act.call(player);
        }
    }

}
