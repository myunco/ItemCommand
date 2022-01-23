package ml.mcos.itemcommand.action;

import ml.mcos.itemcommand.config.Language;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ActionBarAction extends Action {
    private final boolean all;

    public ActionBarAction(String value) {
        this(value, false);
    }

    public ActionBarAction(String value, boolean all) {
        super(value);
        this.all = all;
    }

    @Override
    public void execute(Player player) {
        if (all) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                try {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(plugin.replacePlaceholders(p, value)));
                } catch (Exception e) {
                    plugin.logMessage(Language.actionExecuteErrorActionBarNotSupport);
                    return;
                }
            }
        } else {
            try {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(plugin.replacePlaceholders(player, value)));
            } catch (Exception e) {
                //TODO
                e.printStackTrace();
                plugin.logMessage(Language.actionExecuteErrorActionBarNotSupport);
            }
        }
    }
}
