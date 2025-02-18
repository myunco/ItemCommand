package net.myunco.itemcommand.action;

import net.myunco.itemcommand.ItemCommand;
import net.myunco.itemcommand.config.Language;
import net.myunco.itemcommand.util.Utils;
import org.bukkit.entity.Player;

import java.util.Random;

public class Action {
    public static ItemCommand plugin = ItemCommand.getPlugin();
    protected final String value;
    protected int delay;
    protected int probability = 100;
    protected long seed;

    public Action(String value) {
        this.value = parse(value);
    }

    private String parse(String value) {
        String[] v = value.split(" / ");
        if (v.length > 1) {
            delay = Utils.parseInt(v[1]);
            if (delay == -1) {
                plugin.logMessage(Language.replaceArgs(Language.actionParseErrorDelay, v[1]));
            }
            if (v.length > 2) {
                if (v[2].indexOf(':') == -1) {
                    probability = Utils.parseInt(v[2]);
                    if (probability == -1) {
                        plugin.logMessage(Language.replaceArgs(Language.actionParseErrorProbability, v[2]));
                    }
                } else {
                    probability = Utils.parseInt(Utils.getTextLeft(v[2], ':'));
                    if (probability == -1) {
                        plugin.logMessage(Language.replaceArgs(Language.actionParseErrorProbability, v[2]));
                    }
                    seed = Utils.parseInt(Utils.getTextRight(v[2], ':'));
                    if (probability == -1) {
                        plugin.logMessage(Language.replaceArgs(Language.actionParseErrorSeed, v[2]));
                    }
                }
            }
        }
        return v[0];
    }

    public void call (Player player) {
        if (probability <= 0) { // 概率为0，不执行
            return;
        }
        if (probability < 100) {
            Random random;
            if (seed != 0) {
                random = new Random(System.currentTimeMillis() / 100 + seed);
            } else {
                random = new Random();
            }
            if (random.nextInt(100) + 1 > probability) {
                return;
            }
        }
        if (delay > 0) {
            plugin.getScheduler().runTaskLater(() -> execute(player), delay);
        } else {
            execute(player);
        }
    }
    public void execute(Player player) {}
}
