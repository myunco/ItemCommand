package net.myunco.itemcommand.action;

import net.myunco.itemcommand.config.Language;
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
        if (plugin.mcVersion < 9) {
            plugin.logMessage(Language.actionExecuteErrorActionBarNotSupport);
            return;
        }
        String msg = plugin.replacePlaceholders(player, value);
        try {
            if (all) {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
                }
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
            }
        } catch (Throwable t) {
            plugin.logMessage(Language.actionExecuteErrorActionBarNotSupport);
        }
    }
}
