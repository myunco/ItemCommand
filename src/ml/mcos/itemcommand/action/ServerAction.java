package ml.mcos.itemcommand.action;

import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerAction extends Action {

    public ServerAction(String value) {
        super(value);
    }

    @Override
    public void execute(Player player) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(byteOut);
        try {
            dataOut.writeUTF("Connect");
            dataOut.writeUTF(plugin.replacePlaceholders(player, value));
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", byteOut.toByteArray());
    }
}
