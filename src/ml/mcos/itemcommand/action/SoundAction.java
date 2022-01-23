package ml.mcos.itemcommand.action;

import ml.mcos.itemcommand.config.Language;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundAction extends Action {

    public SoundAction(String value) {
        super(value);
    }

    @Override
    public void execute(Player player) {
        try {
            player.playSound(player.getLocation(), Sound.valueOf(value), 1F, 1F);
        } catch (IllegalArgumentException e) {
            plugin.logMessage(Language.replaceArgs(Language.actionExecuteErrorSound, value));
        }
    }
}
