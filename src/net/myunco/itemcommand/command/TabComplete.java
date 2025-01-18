package net.myunco.itemcommand.command;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TabComplete {
    public static HashMap<String, List<String>> tabListMap = new HashMap<>();
    public static List<String> amountList = new ArrayList<>(64);
    public static List<String> typeList = Arrays.stream(Material.values()).map(Material::toString).collect(Collectors.toList());

    static {
        tabListMap.put("ItemCommand", Arrays.asList("add", "give", "list", "reload", "type", "version"));
        tabListMap.put("ItemCommand.give", Collections.emptyList());
        for (int i = 1; i <= 64; i++) {
            amountList.add(String.valueOf(i));
        }
    }

    public static List<String> getTabList(String[] args, String command) {
        StringBuilder builder = new StringBuilder(command);
        for (int i = 1; i < args.length; i++) {
            builder.append(".").append(args[i - 1].toLowerCase());
        }
        return tabListMap.get(builder.toString());
    }

    public static List<String> getCompleteList(String[] args, List<String> list) {
        return getCompleteList(args, list, false);
    }

    public static List<String> getCompleteList(String[] args, List<String> list, boolean listToLowerCase) {
        List<String> ret = new ArrayList<>();
        if (list == null) {
            return ret;
        } else if (list.isEmpty()) {
            return null;
        } else if (args[args.length - 1].isEmpty()) {
            return list;
        }
        String arg = args[args.length - 1].toLowerCase();
        for (String value : list) {
            if (listToLowerCase) {
                if (value.toLowerCase().startsWith(arg)) {
                    ret.add(value);
                }
            } else {
                if (value.startsWith(arg)) {
                    ret.add(value);
                }
            }
        }
        return ret;
    }
}
