package ml.mcos.itemcommand.item.expression;

import ml.mcos.itemcommand.ItemCommand;
import ml.mcos.itemcommand.action.Action;
import ml.mcos.itemcommand.action.ActionBarAction;
import ml.mcos.itemcommand.action.BroadcastAction;
import ml.mcos.itemcommand.action.ChatAction;
import ml.mcos.itemcommand.action.CommandAction;
import ml.mcos.itemcommand.action.ConsoleAction;
import ml.mcos.itemcommand.action.GiveMoneyAction;
import ml.mcos.itemcommand.action.GivePointsAction;
import ml.mcos.itemcommand.action.OperatorAction;
import ml.mcos.itemcommand.action.ServerAction;
import ml.mcos.itemcommand.action.SoundAction;
import ml.mcos.itemcommand.action.TellAction;
import ml.mcos.itemcommand.action.TitleAction;
import ml.mcos.itemcommand.util.Utils;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public interface Expression {
    ItemCommand plugin = ItemCommand.getPlugin();

    boolean execute(Player player);

    void executeAction(Player player);

    static String getOperator(String expression) {
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

    static ArrayList<String> parseCondition(String condition) {
        ArrayList<String> args = new ArrayList<>();
        // args 1 = 表达式, 2 = 动作1, 3 = 动作2 ...
        StringBuilder currentPart = new StringBuilder();
        boolean isEscaping = false;
        for (int i = 0; i < condition.length(); i++) {
            char c = condition.charAt(i);
            if (isEscaping) {
                currentPart.append(c);
                isEscaping = false;
            } else if (c == '\\') {
                isEscaping = true;
            } else if (c == ',') {
                if (i + 1 == condition.length()) {
                    break;
                }
                args.add(currentPart.toString().trim());
                currentPart = new StringBuilder();
            } else {
                currentPart.append(c);
            }

        }
        if (currentPart.length() != 0) {
            args.add(currentPart.toString().trim());
        }
        return args;
    }

    static Action[] parseAction(ArrayList<String> args) {
        Action[] action = new Action[args.size() - 1];
        if (action.length != 0) {
            for (int i = 1; i < args.size(); i++) {
                String actionType = Utils.getTextLeft(args.get(i), ':');
                if (actionType.isEmpty()) {
                    action[i - 1] = new TellAction(args.get(i));
                } else {
                    String actionValue = Utils.getTextRight(args.get(i), ':').trim();
                    switch (actionType.toLowerCase()) {
                        case "cmd":
                            action[i - 1] = new CommandAction(actionValue);
                            break;
                        case "op":
                            action[i - 1] = new OperatorAction(actionValue);
                            break;
                        case "console":
                            action[i - 1] = new ConsoleAction(actionValue);
                            break;
                        case "tell":
                            action[i - 1] = new TellAction(actionValue);
                            break;
                        case "chat":
                            action[i - 1] = new ChatAction(actionValue);
                            break;
                        case "sound":
                            action[i - 1] = new SoundAction(actionValue);
                            break;
                        case "sound-all":
                            action[i - 1] = new SoundAction(actionValue, true);
                            break;
                        case "broadcast":
                            action[i - 1] = new BroadcastAction(actionValue);
                            break;
                        case "give-money":
                            action[i - 1] = new GiveMoneyAction(actionValue);
                            break;
                        case "give-points":
                            action[i - 1] = new GivePointsAction(actionValue);
                            break;
                        case "title":
                            action[i - 1] = new TitleAction(actionValue);
                            break;
                        case "title-all":
                            action[i - 1] = new TitleAction(actionValue, true);
                            break;
                        case "action-bar":
                            action[i - 1] = new ActionBarAction(actionValue);
                            break;
                        case "action-bar-all":
                            action[i - 1] = new ActionBarAction(actionValue, true);
                            break;
                        case "server":
                            action[i - 1] = new ServerAction(actionValue);
                            break;
                        default:
                            action[i - 1] = new TellAction(args.get(i));
                    }
                }
            }
        }
        return action;
    }

}
