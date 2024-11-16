package net.myunco.itemcommand.action;

import net.myunco.itemcommand.config.Language;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class SoundAction extends Action {
    private final boolean all;

    public SoundAction(String value) {
        this(value, false);
    }

    public SoundAction(String value, boolean all) {
        super(value);
        this.all = all;
    }

    @Override
    public void execute(Player player) {
        try {
            if (all) {
                for (Player p : getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.valueOf(value), 1F, 1F);
                }
            } else {
                player.playSound(player.getLocation(), Sound.valueOf(value), 1F, 1F);
            }
        } catch (IllegalArgumentException e) {
            plugin.logMessage(Language.replaceArgs(Language.actionExecuteErrorSound, value));
        }
    }

    public Collection<? extends Player> getOnlinePlayers() {
        if (plugin.mcVersion > 7 || (plugin.mcVersion == 7 && plugin.mcVersionPatch == 10)) {
            return plugin.getServer().getOnlinePlayers();
        }
        try {
            return Arrays.asList((Player[]) Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers").invoke(plugin.getServer()));
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
