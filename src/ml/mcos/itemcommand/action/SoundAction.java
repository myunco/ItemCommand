package ml.mcos.itemcommand.action;

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
            plugin.getLogger().warning("错误: 指定的音效 " + value + " 不存在");
        }
    }
}
